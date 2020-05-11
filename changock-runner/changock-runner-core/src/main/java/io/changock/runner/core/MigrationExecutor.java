package io.changock.runner.core;

import io.changock.driver.api.common.DependencyInjectionException;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeState;
import io.changock.driver.api.lock.LockManager;
import io.changock.migration.api.ChangeLogItem;
import io.changock.migration.api.ChangeSetItem;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static io.changock.driver.api.entry.ChangeState.EXECUTED;
import static io.changock.driver.api.entry.ChangeState.FAILED;
import static io.changock.driver.api.entry.ChangeState.IGNORED;

@NotThreadSafe
public class MigrationExecutor<CHANGE_ENTRY extends ChangeEntry> {

  private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);

  protected final ConnectionDriver driver;
  protected final Map<String, Object> metadata;
  protected final DependencyManager dependencyManager;
  private final long maxWaitingForLockMinutes;
  private final int maxTries;
  private final long lockAcquiredForMinutes;
  protected boolean executionInProgress = false;

  public MigrationExecutor(ConnectionDriver<CHANGE_ENTRY> driver,
                           DependencyManager dependencyManager,
                           long lockAcquiredForMinutes,
                           int maxTries,
                           long maxWaitingForLockMinutes,
                           Map<String, Object> metadata) {
    this.driver = driver;
    this.metadata = metadata;
    this.dependencyManager = dependencyManager;
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
  }

  public boolean isExecutionInProgress() {
    return this.executionInProgress;
  }

  public void executeMigration(List<ChangeLogItem> changeLogs) {
    initializationAndValidation();
    try (LockManager lockManager = driver.getLockManager()) {
      lockManager.acquireLockDefault();
      String executionId = generateExecutionId();
      logger.info("Changock starting the data migration sequence id[{}]...", executionId);
      for (ChangeLogItem changeLog : changeLogs) {
        for (ChangeSetItem changeSet : changeLog.getChangeSetElements()) {
          try {
            executeAndLogChangeSet(executionId, changeLog.getInstance(), changeSet);
          } catch (Exception e) {
            processExceptionOnChangeSetExecution(e, changeSet.getMethod(), changeSet.isFailFast());
          }
        }

      }
    } finally {
      this.executionInProgress = false;
      logger.info("Changock has finished his job.");
    }
  }

  protected String generateExecutionId() {
    return String.format("%s.%s", LocalDateTime.now().toString(), UUID.randomUUID().toString());
  }

  protected void executeAndLogChangeSet(String executionId, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    ChangeEntry changeEntry = null;
    try {
      if (changeSetItem.isRunAlways() || driver.getChangeEntryService().hasNotBennExecuted(changeSetItem.getId(), changeSetItem.getAuthor())) {
        final long executionTimeMillis = executeChangeSetMethod(changeSetItem.getMethod(), changelogInstance);
        changeEntry = createChangeEntryInstance(executionId, changeSetItem, executionTimeMillis, EXECUTED);

      } else {
        changeEntry = createChangeEntryInstance(executionId, changeSetItem, -1L, IGNORED);

      }
    } catch (Exception ex) {
      changeEntry = createChangeEntryInstance(executionId, changeSetItem, -1L, FAILED);
      throw ex;
    } finally {
      if (changeEntry != null) {
        logChangeEntry(changeEntry, changeSetItem);
        driver.getChangeEntryService().save(changeEntry);
      }
    }
  }

  protected void logChangeEntry(ChangeEntry changeEntry, ChangeSetItem changeSetItem) {
    switch (changeEntry.getState()) {
      case EXECUTED:
        if (changeSetItem.isRunAlways()) {
          logger.info("RE-APPLIED - {}", changeEntry);

        } else {
          logger.info("APPLIED - {}", changeEntry);
        }
        break;
      case IGNORED:
        logger.info("PASSED OVER - {}", changeSetItem);
        break;
      case FAILED:
        logger.info("FAILED OVER - {}", changeSetItem);
        break;
    }
  }

  protected ChangeEntry createChangeEntryInstance(String executionId, ChangeSetItem changeSetItem, long executionTimeMillis, ChangeState state) {
    return ChangeEntry.createInstance(executionId, state, changeSetItem, executionTimeMillis, metadata);
  }

  protected long executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance) throws IllegalAccessException, InvocationTargetException {
    final long startingTime = System.currentTimeMillis();
    List<Object> changelogInvocationParameters = new ArrayList<>(changeSetMethod.getParameterTypes().length);
    for (Class<?> parameterType : changeSetMethod.getParameterTypes()) {
      Optional<Object> parameterOptional = dependencyManager.getDependency(parameterType);
      if (parameterOptional.isPresent()) {
        changelogInvocationParameters.add(parameterOptional.get());
      } else {
        throw new DependencyInjectionException(parameterType);
      }
    }
    LogUtils.logMethodWithArguments(logger, changeSetMethod.getName(), changelogInvocationParameters);
    changeSetMethod.invoke(changeLogInstance, changelogInvocationParameters.toArray());
    return System.currentTimeMillis() - startingTime;
  }

  protected void processExceptionOnChangeSetExecution(Exception exception, Method method, boolean throwException) {
    String exceptionMsg = exception instanceof InvocationTargetException
        ? ((InvocationTargetException) exception).getTargetException().getMessage()
        : exception.getMessage();
    String finalMessage = String.format("Error in method[%s.%s] : %s", method.getDeclaringClass().getSimpleName(), method.getName(), exceptionMsg);
    if (throwException) {
      throw new ChangockException(finalMessage, exception);

    } else {
      logger.warn(finalMessage, exception);
    }
  }

  @SuppressWarnings("unchecked")
  protected void initializationAndValidation() throws ChangockException {
    this.executionInProgress = true;
    driver.setLockSettings(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
    driver.initialize();
    driver.runValidation();
    this.dependencyManager
        .addDriverDependencies(driver.getDependencies())
        .addForbiddenParameters(driver.getForbiddenParameters());
  }

}
