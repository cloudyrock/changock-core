package io.changock.runner.core;

import io.changock.driver.api.entry.ChangeState;
import io.changock.migration.api.ChangeLogItem;
import io.changock.migration.api.ChangeSetItem;
import io.changock.driver.api.common.Validable;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.lock.LockManager;
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

@NotThreadSafe
public class MigrationExecutor implements Validable {

  private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);

  protected final ConnectionDriver driver;
  protected final Map<String, Object> metadata;
  protected final DependencyManager dependencyManager;
  private final long maxWaitingForLockMinutes;
  private final int maxTries;
  private final long lockAcquiredForMinutes;
  protected boolean executionInProgress = false;

  public MigrationExecutor(ConnectionDriver driver,
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
    initialize();
    runValidation();
    try (LockManager lockManager = driver.getLockManager()) {
      lockManager.acquireLockDefault();
      String executionId = generateExecutionId();
      logger.info("Changock starting the data migration sequence id[{}]...", executionId);
      for (ChangeLogItem changeLog : changeLogs) {
        for (ChangeSetItem changeSet : changeLog.getChangeSetElements()) {
          try {
            executeChangeSet(executionId, changeLog.getInstance(), changeSet);
          } catch (Exception e) {
            processExceptionOnChangeSetExecution(e, changeSet.isFailFast());
          }
        }

      }
    } finally {
      this.executionInProgress = false;
      logger.info("Changock has finished his job.");
    }
  }

  protected void initialize() {
    this.executionInProgress = true;
    driver.setLockSettings(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
    driver.initialize();
    this.dependencyManager.addConnectorDependency(driver.getDependencies());
  }

  protected String generateExecutionId() {
    return String.format("%s.%s", LocalDateTime.now().toString(), UUID.randomUUID().toString());
  }

  protected void executeChangeSet(String executionId, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    if (driver.getChangeEntryService().isNewChange(changeSetItem.getId(), changeSetItem.getAuthor())) {
      final long executionTimeMillis = executeChangeSetMethod(changeSetItem.getMethod(), changelogInstance);
      ChangeEntry changeEntry = ChangeEntry.createInstance(executionId, ChangeState.EXECUTED, changeSetItem, executionTimeMillis, metadata);
      driver.getChangeEntryService().save(changeEntry);
      logger.info("APPLIED - {}", changeEntry);

    } else if (changeSetItem.isRunAlways()) {
      final long executionTimeMillis = executeChangeSetMethod(changeSetItem.getMethod(), changelogInstance);
      ChangeEntry changeEntry = ChangeEntry.createInstance(executionId, ChangeState.EXECUTED, changeSetItem, executionTimeMillis, metadata);
      driver.getChangeEntryService().save(changeEntry);
      logger.info("RE-APPLIED - {}", changeEntry);

    } else {
      logger.info("PASSED OVER - {}", changeSetItem);
    }
  }

  protected long executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance) throws IllegalAccessException, InvocationTargetException {
    final long startingTime = System.currentTimeMillis();
    List<Object> changelogInvocationParameters = new ArrayList<>(changeSetMethod.getParameterTypes().length);
    for (Class<?> parameterType : changeSetMethod.getParameterTypes()) {
      Optional<Object> parameterOptional = dependencyManager.getDependency(parameterType);
      if (parameterOptional.isPresent()) {
        changelogInvocationParameters.add(parameterOptional.get());
      } else {
        throw new DependencyInjectionException(changeSetMethod.getName(), parameterType.getName());
      }
    }
    LogUtils.logMethodWithArguments(logger, changeSetMethod.getName(), changelogInvocationParameters);
    changeSetMethod.invoke(changeLogInstance, changelogInvocationParameters.toArray());
    return System.currentTimeMillis() - startingTime;
  }

  protected void processExceptionOnChangeSetExecution(Exception exception, boolean throwException) {
    String message;
    if (exception instanceof InvocationTargetException) {
      message =  ((InvocationTargetException)exception).getTargetException().getMessage();

    } else if (exception instanceof DependencyInjectionException) {
      DependencyInjectionException ex = (DependencyInjectionException)exception;
      message = String.format("Method[%s] using argument[%s] not injected", ex.getMethod(), ex.getParameterType());

    } else {
      message = exception.getMessage();
    }
    if(throwException)  {
      throw new ChangockException(message, exception);

    } else {
      logger.warn(message, exception);
    }
  }

  @Override
  public void runValidation() throws ChangockException {
    driver.runValidation();
  }
}
