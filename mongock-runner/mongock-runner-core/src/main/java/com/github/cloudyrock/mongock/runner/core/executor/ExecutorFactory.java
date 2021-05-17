package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.change.MigrationExecutorImpl;
import com.github.cloudyrock.mongock.runner.core.executor.change.MigrationOp;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;

public class ExecutorFactory {

  public Executor getExecutor(Operation op,
                              ConnectionDriver driver,
                              DependencyManager dependencyManager,
                              Map<String, Object> metadata,
                              Function<Parameter, String> parameterNameProvider,
                              MongockConfiguration config) {
    switch (op.getId()) {
      case MigrationOp.ID:
        return new MigrationExecutorImpl(driver, dependencyManager, metadata, parameterNameProvider, config);
      default:
        throw new MongockException(String.format("Operation [%s] not supported", op.getId()));
    }
  }


}
