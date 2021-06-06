package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.mongock.runner.core.executor.operation.list.ListChangesExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.operation.list.ListChangesOp;

import java.lang.reflect.Parameter;
import java.util.SortedSet;
import java.util.function.Function;

public class ExecutorFactoryImpl<R>
implements ExecutorFactory<ChangeLogItem, ChangeEntry, MongockConfiguration, R>{

  @SuppressWarnings("unchecked")
  public Executor<R> getExecutor(Operation<R> op,
                                 SortedSet<ChangeLogItem> changeLogs,
                                 ConnectionDriver<ChangeEntry> driver,
                                 DependencyManager dependencyManager,
                                 Function<Parameter, String> parameterNameProvider,
                                 MongockConfiguration config) {
    switch (op.getId()) {

      case MigrationOp.ID:
        return (Executor<R>) new MigrationExecutor(changeLogs, driver, dependencyManager, parameterNameProvider, config);

      case ListChangesOp.ID:
        return (Executor<R>) new ListChangesExecutor();

      default:
        throw new MongockException("Executor not found for operation: " + op.getId());
    }
  }


}


