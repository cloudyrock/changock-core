package com.github.cloudyrock.spring.util;

import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;

public class SpringMigrationExecutor<CHANGE_ENTRY extends ChangeEntry> extends SpringMigrationExecutorBase<CHANGE_ENTRY> {
  private final Function<Parameter, String> parameterNameProvider;
  private final TransactionExecutor transactionExecutor;

  public SpringMigrationExecutor(
      ConnectionDriver driver,
      DependencyManager dependencyManager,
      MigrationExecutorConfiguration config,
      Map<String, Object> metadata,
      Function<Parameter, String> parameterNameProvider,
      TransactionExecutor transactionExecutor) {
    super(driver, dependencyManager, config, metadata);
    this.transactionExecutor = transactionExecutor;
    this.parameterNameProvider = parameterNameProvider;
  }

  @Override
  public void executeAndLogChangeSet(String executionId, String executionHostname, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    transactionExecutor.executionInTransaction(() ->     {
      try {
        super.executeAndLogChangeSet(executionId, executionHostname, changelogInstance, changeSetItem);
      } catch (Exception e) {
        throw e instanceof MongockException ? (MongockException) e : new MongockException(e);
      }

    });
  }

  @Override
  protected String getParameterName(Parameter parameter) {
    return parameterNameProvider.apply(parameter);
  }
}
