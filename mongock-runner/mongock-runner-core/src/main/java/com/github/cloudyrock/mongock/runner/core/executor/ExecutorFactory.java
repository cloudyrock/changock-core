package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.migration.ExecutorConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.migration.MigrationExecutorImpl;
import com.github.cloudyrock.mongock.runner.core.executor.migration.MigrationOp;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;

public class ExecutorFactory<CONFIG extends ExecutorConfiguration> {

  public Executor getExecutor(Operation op,
                              ConnectionDriver driver,
                              DependencyManager dependencyManager,
                              CONFIG configuration,
                              Map<String, Object> metadata,
                              Function<Parameter, String> parameterNameProvider) {
    switch (op.getId()) {
      case MigrationOp.ID:
        return new MigrationExecutorImpl(driver, dependencyManager, configuration, metadata, parameterNameProvider);
      default:
        throw new MongockException(String.format("Operation [%s] not supported", op.getId()));
    }
  }


}
