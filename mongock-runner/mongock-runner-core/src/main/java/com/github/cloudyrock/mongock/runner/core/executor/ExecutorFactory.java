package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.config.executor.ExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.executor.change.MigrationExecutorImpl;
import com.github.cloudyrock.mongock.runner.core.executor.change.MigrationOp;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.list.ListExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.list.ListOp;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ExecutorFactory<CONFIG extends ExecutorConfiguration> {


  //todo yodo Operation equal/hash
  //todo getMappers at construction time
  @SuppressWarnings("unchecked")
  public <M> Executor<M> getExecutor(Operation<M> op,
                                     ConnectionDriver driver,
                                     DependencyManager dependencyManager,
                                     Function<Parameter, String> parameterNameProvider,
                                     CONFIG config) {
    return getExecutorMappers().get(op).getExecutor(driver, dependencyManager, parameterNameProvider, config);
//    return getExecutorMappers().get(op).executor;
//    switch (op.getId()) {
//      case MigrationOp.ID:
//        return new MigrationExecutorImpl(driver, dependencyManager, parameterNameProvider, config);
//      case ListOp.ID:
//        return new ListExecutor();
//      default:
//        throw new MongockException(String.format("Operation [%s] not supported", op.getId()));
//    }
  }



  private Map<Operation, ExecutorOperationMapper> getExecutorMappers() {
    Map<Operation, ExecutorOperationMapper> map = new HashMap<>();
    map.put(new MigrationOp(), getMigrationExecutorMapper());
    map.put(new ListOp(), getListExecutorMapper());
    return map;
  }

  private ExecutorOperationMapper<Boolean> getMigrationExecutorMapper() {
    return new ExecutorOperationMapper<Boolean>(new MigrationOp()) {
      @Override
      public Executor<Boolean> getExecutor(ConnectionDriver driver, DependencyManager dependencyManager, Function<Parameter, String> parameterNameProvider, CONFIG config) {
        return new MigrationExecutorImpl(driver, dependencyManager, parameterNameProvider, config);
      }
    };
  }

  private ExecutorOperationMapper<List> getListExecutorMapper() {
    return new ExecutorOperationMapper<List>(new ListOp()) {
      @Override
      public Executor<List> getExecutor(ConnectionDriver driver, DependencyManager dependencyManager, Function<Parameter, String> parameterNameProvider, CONFIG config) {
        return new ListExecutor();
      }
    };
  }


  private abstract class ExecutorOperationMapper<T> {
    public Operation<T> operation;

    public ExecutorOperationMapper(Operation<T> operation) {
      this.operation = operation;
    }

    public abstract Executor<T> getExecutor(ConnectionDriver driver, DependencyManager dependencyManager, Function<Parameter, String> parameterNameProvider, CONFIG config);
  }


}


