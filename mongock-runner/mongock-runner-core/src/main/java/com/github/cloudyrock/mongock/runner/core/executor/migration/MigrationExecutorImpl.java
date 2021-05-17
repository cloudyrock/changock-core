package com.github.cloudyrock.mongock.runner.core.executor.migration;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;

@NotThreadSafe
public class MigrationExecutorImpl extends MigrationExecutorBase<ExecutorConfiguration>{


  public MigrationExecutorImpl(ConnectionDriver driver,
                               DependencyManager dependencyManager,
                               ExecutorConfiguration mongockConfiguration,
                               Map<String, Object> metadata,
                               Function<Parameter, String> parameterNameProvider) {
    super(driver, dependencyManager, mongockConfiguration, metadata, parameterNameProvider);
  }
}
