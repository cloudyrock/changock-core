package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.driver.api.common.DependencyInjectionException;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.driver.TransactionStrategy;
import com.github.cloudyrock.mongock.driver.api.driver.Transactionable;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeState;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import io.changock.migration.api.annotations.NonLockGuarded;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;

import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.EXECUTED;
import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.FAILED;
import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.IGNORED;

@NotThreadSafe
public class MigrationExecutor<CHANGE_ENTRY extends ChangeEntry> {

  private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);

  protected final ConnectionDriver driver;
  protected final Map<String, Object> metadata;
  protected final DependencyManager dependencyManager;
  private final MigrationExecutorConfiguration config;
  protected boolean executionInProgress = false;

  public MigrationExecutor(ConnectionDriver<CHANGE_ENTRY> driver,
                           DependencyManager dependencyManager,
                           MigrationExecutorConfiguration config,
                           Map<String, Object> metadata) {
    this.driver = driver;
    this.metadata = metadata;
    this.dependencyManager = dependencyManager;
    this.config = config;
  }

  public boolean isExecutionInProgress() {
    return this.executionInProgress;
  }

  public void executeMigration(SortedSet<ChangeLogItem> changeLogs) {
    initializationAndValidation();
    try (LockManager lockManager = driver.getLockManager()) {
      if(!this.isThereAnyChangeSetItemToBeExecuted(changeLogs)) {
        logger.info("Mongock skipping the data migration. All change set items are already executed or there is no change set item.");
        return;
      }
      lockManager.acquireLockDefault();
      executeInTransactionIfStrategyOrUsualIfNot(TransactionStrategy.MIGRATION, () -> processAllChangeLogs(changeLogs));
    } finally {
      this.executionInProgress = false;
      logger.info("Mongock has finished");
    }
  }

  protected void executeInTransactionIfStrategyOrUsualIfNot(TransactionStrategy strategy, Runnable operation) {
    if (driver instanceof Transactionable && ((Transactionable)driver).getTransactionStrategy() == strategy) {
      ((Transactionable)driver).executeInTransaction(operation);
    } else {
      operation.run();
    }
  }

  protected void processAllChangeLogs(SortedSet<ChangeLogItem> changeLogs) {
    String executionId = generateExecutionId();
    logger.info("Mongock starting the data migration sequence id[{}]...", executionId);
    for (ChangeLogItem changeLog : changeLogs) {
      executeInTransactionIfStrategyOrUsualIfNot(TransactionStrategy.CHANGE_LOG, () -> processSingleChangeLog(executionId, changeLog));
    }
  }

  protected void processSingleChangeLog(String executionId, ChangeLogItem changeLog) {
    for (ChangeSetItem changeSet : changeLog.getChangeSetElements()) {
      executeInTransactionIfStrategyOrUsualIfNot(TransactionStrategy.CHANGE_SET, () -> processSingleChangeSet(executionId, changeLog, changeSet));
    }
  }

  protected void processSingleChangeSet(String executionId, ChangeLogItem changeLog, ChangeSetItem changeSet) {
    try {
      executeAndLogChangeSet(executionId, changeLog.getInstance(), changeSet);
    } catch (Exception e) {
      processExceptionOnChangeSetExecution(e, changeSet.getMethod(), changeSet.isFailFast());
    }
  }

  protected String generateExecutionId() {
    return String.format("%s-%d", LocalDateTime.now().toString(), new Random().nextInt(999));
  }

  protected boolean isThereAnyChangeSetItemToBeExecuted(SortedSet<ChangeLogItem> changeLogs) {
    return changeLogs.stream()
        .map(ChangeLogItem::getChangeSetElements)
        .flatMap(List::stream)
        .anyMatch(changeSetItem -> changeSetItem.isRunAlways() || !this.isAlreadyExecuted(changeSetItem));
  }

  protected boolean isAlreadyExecuted(ChangeSetItem changeSetItem) {
    return driver.getChangeEntryService().isAlreadyExecuted(changeSetItem.getId(), changeSetItem.getAuthor());
  }

  protected void executeAndLogChangeSet(String executionId, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    ChangeEntry changeEntry = null;
    boolean alreadyExecuted = false;
    try {
      if (!(alreadyExecuted = isAlreadyExecuted(changeSetItem)) || changeSetItem.isRunAlways()) {
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
        logChangeEntry(changeEntry, changeSetItem, alreadyExecuted);
        // if not runAlways or, being runAlways, it hasn't been executed before
        if (!changeSetItem.isRunAlways() || !alreadyExecuted) {
          //if not ignored or, being ignored, should be tracked anyway
          if (changeEntry.getState() != IGNORED || config.isTrackIgnored()) {
            driver.getChangeEntryService().save(changeEntry);
          }
        }
      }
    }
  }

  private void logChangeEntry(ChangeEntry changeEntry, ChangeSetItem changeSetItem, boolean alreadyExecuted) {
    switch (changeEntry.getState()) {
      case EXECUTED:
        logger.info("{}APPLIED - {}", alreadyExecuted ? "RE-" : "", changeEntry.toPrettyString());
        break;
      case IGNORED:
        logger.info("PASSED OVER - {}", changeSetItem.toPrettyString());
        break;
      case FAILED:
        logger.info("FAILED OVER - {}", changeSetItem.toPrettyString());
        break;
    }
  }

  protected ChangeEntry createChangeEntryInstance(String executionId, ChangeSetItem changeSetItem, long executionTimeMillis, ChangeState state) {
    return ChangeEntry.createInstance(executionId, state, changeSetItem, executionTimeMillis, metadata);
  }

  protected long executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance) throws IllegalAccessException, InvocationTargetException {
    final long startingTime = System.currentTimeMillis();
    Class<?>[] parameterTypes = changeSetMethod.getParameterTypes();
    Parameter[] parameters = changeSetMethod.getParameters();
    List<Object> changelogInvocationParameters = new ArrayList<>(parameterTypes.length);
    for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {
      changelogInvocationParameters.add(getParameter(parameterTypes[paramIndex], parameters[paramIndex]));
    }
    LogUtils.logMethodWithArguments(logger, changeSetMethod.getName(), changelogInvocationParameters);
    changeSetMethod.invoke(changeLogInstance, changelogInvocationParameters.toArray());
    return System.currentTimeMillis() - startingTime;
  }

  protected Object getParameter(Class<?> parameterType, Parameter parameter) {
    String name = getParameterName(parameter);
    return dependencyManager
        .getDependency(parameterType, name, !parameterType.isAnnotationPresent(NonLockGuarded.class) && !parameter.isAnnotationPresent(NonLockGuarded.class))
        .orElseThrow(() -> new DependencyInjectionException(parameterType, name));
  }

  protected String getParameterName(Parameter parameter) {
    return parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
  }

  protected void processExceptionOnChangeSetExecution(Exception exception, Method method, boolean throwException) {
    String exceptionMsg = exception instanceof InvocationTargetException
        ? ((InvocationTargetException) exception).getTargetException().getMessage()
        : exception.getMessage();
    String finalMessage = String.format("Error in method[%s.%s] : %s", method.getDeclaringClass().getSimpleName(), method.getName(), exceptionMsg);
    if (throwException) {
      throw new MongockException(finalMessage, exception);

    } else {
      logger.warn(finalMessage, exception);
    }
  }

  @SuppressWarnings("unchecked")
  protected void initializationAndValidation() throws MongockException {
    this.executionInProgress = true;
    driver.initialize();
    driver.runValidation();
    this.dependencyManager
        .setLockGuardProxyFactory(new LockGuardProxyFactory(driver.getLockManager()))
        .addDriverDependencies(driver.getDependencies())
        .addForbiddenParameters(driver.getForbiddenParameters());
  }

}
