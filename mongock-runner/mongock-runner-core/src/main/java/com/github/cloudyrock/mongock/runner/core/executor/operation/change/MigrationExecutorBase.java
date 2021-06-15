package com.github.cloudyrock.mongock.runner.core.executor.operation.change;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.config.executor.ChangeExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.common.DependencyInjectionException;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.driver.Transactioner;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeState;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.utils.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.EXECUTED;
import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.FAILED;
import static com.github.cloudyrock.mongock.driver.api.entry.ChangeState.IGNORED;

@NotThreadSafe
public abstract class MigrationExecutorBase<
    CHANGELOG extends ChangeLogItem<CHANGESET>,
    CHANGESET extends ChangeSetItem,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends ChangeExecutorConfiguration> implements Executor<Boolean> {

  private static final Logger logger = LoggerFactory.getLogger(MigrationExecutorBase.class);
  protected final ConnectionDriver<CHANGE_ENTRY> driver;
  protected final String serviceIdentifier;
  protected final boolean trackIgnored;
  protected final SortedSet<CHANGELOG> changeLogs;
  protected final Map<String, Object> metadata;
  private final DependencyManager dependencyManager;
  private final Function<Parameter, String> parameterNameProvider;
  private boolean executionInProgress = false;
  private final String executionId;

  public MigrationExecutorBase(String executionId,
                               SortedSet<CHANGELOG> changeLogs,
                               ConnectionDriver<CHANGE_ENTRY> driver,
                               DependencyManager dependencyManager,
                               Function<Parameter, String> parameterNameProvider,
                               CONFIG config) {
    this.executionId = executionId;
    this.driver = driver;
    this.dependencyManager = dependencyManager;
    this.parameterNameProvider = parameterNameProvider;
    this.metadata = config.getMetadata();
    this.serviceIdentifier = config.getServiceIdentifier();
    this.trackIgnored = config.isTrackIgnored();
    this.changeLogs = changeLogs;
  }

  public boolean isExecutionInProgress() {
    return this.executionInProgress;
  }

  public Boolean executeMigration() {
    initializationAndValidation();
    try (LockManager lockManager = driver.getLockManager()) {
      if (!this.isThereAnyChangeSetItemToBeExecuted(changeLogs)) {
        logger.info("Mongock skipping the data migration. All change set items are already executed or there is no change set item.");
        return false;
      }
      lockManager.acquireLockDefault();
      String executionHostname = generateExecutionHostname(executionId);
      logger.info("Mongock starting the data migration sequence id[{}]...", executionId);
      processPreMigration(changeLogs, executionId, executionHostname);
      processMigration(changeLogs, executionId, executionHostname);
      processPostMigration(changeLogs, executionId, executionHostname);
      return true;
    } finally {
      this.executionInProgress = false;
      logger.info("Mongock has finished");
    }
  }

  protected void processMigration(SortedSet<CHANGELOG> changeLogs, String executionId, String executionHostname) {
    List<CHANGELOG> changeLogsMigration = changeLogs.stream().filter(CHANGELOG::isMigration).collect(Collectors.toList());
    driver.getTransactioner()
        .orElse(Runnable::run)
        .executeInTransaction(() -> processChangeLogs(executionId, executionHostname, changeLogsMigration));
  }

  protected void processPreMigration(SortedSet<CHANGELOG> changeLogs, String executionId, String executionHostname) {
    List<CHANGELOG> changeLogPreMigration = changeLogs.stream().filter(CHANGELOG::isPreMigration).collect(Collectors.toList());
    processChangeLogs(executionId, executionHostname, changeLogPreMigration);
  }

  protected void processPostMigration(SortedSet<CHANGELOG> changeLogs, String executionId, String executionHostname) {
    List<CHANGELOG> changeLogPostMigration = changeLogs.stream().filter(CHANGELOG::isPostMigration).collect(Collectors.toList());
    processChangeLogs(executionId, executionHostname, changeLogPostMigration);
  }

  protected void processChangeLogs(String executionId, String executionHostname, Collection<CHANGELOG> changeLogs) {
    for (CHANGELOG changeLog : changeLogs) {
      processSingleChangeLog(executionId, executionHostname, changeLog);
    }
  }

  protected void processSingleChangeLog(String executionId, String executionHostname, CHANGELOG changeLog) {
    try {
      for (CHANGESET changeSet : changeLog.getChangeSetItems()) {
        processSingleChangeSet(executionId, executionHostname, changeLog, changeSet);
      }
    } catch (Exception e) {
      if (changeLog.isFailFast()) {
        throw e;
      }
    }
  }

  protected void processSingleChangeSet(String executionId, String executionHostname, CHANGELOG changeLog, CHANGESET changeSet) {
    try {
      executeAndLogChangeSet(executionId, executionHostname, changeLog.getInstance(), changeSet);
    } catch (Exception e) {
      processExceptionOnChangeSetExecution(e, changeSet.getMethod(), changeSet.isFailFast());
    }
  }

  protected String generateExecutionHostname(String executionId) {
    String hostname;
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {
      hostname = "unknown-host." + executionId;
    }

    if (StringUtils.isNotEmpty(serviceIdentifier)) {
      hostname += "-";
      hostname += serviceIdentifier;
    }
    return hostname;
  }

  protected boolean isThereAnyChangeSetItemToBeExecuted(SortedSet<CHANGELOG> changeLogs) {
    return changeLogs.stream()
        .map(CHANGELOG::getChangeSetItems)
        .flatMap(List::stream)
        .anyMatch(changeSetItem -> changeSetItem.isRunAlways() || !this.isAlreadyExecuted(changeSetItem));
  }

  protected boolean isAlreadyExecuted(CHANGESET changeSetItem) {
    return driver.getChangeEntryService().isAlreadyExecuted(changeSetItem.getId(), changeSetItem.getAuthor());
  }

  protected  void executeAndLogChangeSet(String executionId, String executionHostname, Object changelogInstance, CHANGESET changeSetItem) throws IllegalAccessException, InvocationTargetException {
    CHANGE_ENTRY changeEntry = null;
    boolean alreadyExecuted = false;
    try {
      if (!(alreadyExecuted = isAlreadyExecuted(changeSetItem)) || changeSetItem.isRunAlways()) {
        final long executionTimeMillis = executeChangeSetMethod(changeSetItem.getMethod(), changelogInstance);
        changeEntry = createChangeEntryInstance(executionId, executionHostname, changeSetItem, executionTimeMillis, EXECUTED);

      } else {
        changeEntry = createChangeEntryInstance(executionId, executionHostname, changeSetItem, -1L, IGNORED);

      }
    } catch (Exception ex) {
      changeEntry = createChangeEntryInstance(executionId, executionHostname, changeSetItem, -1L, FAILED);
      throw ex;
    } finally {
      if (changeEntry != null) {
        logChangeEntry(changeEntry, changeSetItem, alreadyExecuted);
        // if not runAlways or, being runAlways, it hasn't been executed before
        if (!changeSetItem.isRunAlways() || !alreadyExecuted) {
          //if not ignored or, being ignored, should be tracked anyway
          if (changeEntry.getState() != IGNORED || trackIgnored) {
            driver.getChangeEntryService().save(changeEntry);
          }
        }
      }
    }
  }

  private void logChangeEntry(CHANGE_ENTRY changeEntry, CHANGESET changeSetItem, boolean alreadyExecuted) {
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

  protected abstract CHANGE_ENTRY createChangeEntryInstance(String executionId, String executionHostname, CHANGESET changeSetItem, long executionTimeMillis, ChangeState state);

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
    return parameterNameProvider.apply(parameter);
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
        .addDriverDependencies(driver.getDependencies());
    this.dependencyManager.runValidation();
  }



}
