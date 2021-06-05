package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.ChangeLogItemBase;
import com.github.cloudyrock.mongock.config.executor.ExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutorBase;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.mongock.runner.core.executor.operation.list.ListChangesExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.operation.list.ListChangesOp;

import java.lang.reflect.Parameter;
import java.util.SortedSet;
import java.util.function.Function;

public class ExecutorFactory<CHANGELOG extends ChangeLogItemBase, CONFIG extends ExecutorConfiguration, R> {

  @SuppressWarnings("unchecked")
  public Executor<R> getExecutor(Operation<R> op,
                                 SortedSet<CHANGELOG> changeLogs,
                                 ConnectionDriver<?> driver,
                                 DependencyManager dependencyManager,
                                 Function<Parameter, String> parameterNameProvider,
                                 CONFIG config) {
    switch (op.getId()) {

      case MigrationOp.ID:
        return (Executor<R>) new MigrationExecutorBase<>(changeLogs, driver, dependencyManager, parameterNameProvider, config);

      case ListChangesOp.ID:
        return (Executor<R>) new ListChangesExecutor();

      default:
        throw new MongockException("Executor not found for operation: " + op.getId());
    }
  }


}


