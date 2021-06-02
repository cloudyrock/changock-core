package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.config.executor.ExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.mongock.runner.core.executor.operation.list.ListChangesExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.operation.list.ListChangesOp;
import com.github.cloudyrock.mongock.runner.core.executor.operation.list.ListChangesResult;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ExecutorFactory<CONFIG extends ExecutorConfiguration> {

  @SuppressWarnings("unchecked")
  public <M> Executor<M> getExecutor(Operation<M> op,
                                     ConnectionDriver<?> driver,
                                     DependencyManager dependencyManager,
                                     Function<Parameter, String> parameterNameProvider,
                                     CONFIG config) {
    switch (op.getId()) {

      case MigrationOp.ID: return (Executor<M>) new MigrationExecutor(driver, dependencyManager, parameterNameProvider, config);

      case ListChangesOp.ID: return (Executor<M>) new ListChangesExecutor();

      default: throw  new MongockException("Executor not found for operation: " + op.getId());
    }
  }


}


