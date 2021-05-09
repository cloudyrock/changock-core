package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;

@NotThreadSafe
public class MigrationExecutorImpl extends MigrationExecutorBase<MigrationExecutorConfiguration>{


  public MigrationExecutorImpl(ConnectionDriver driver,
                               DependencyManager dependencyManager,
                               MigrationExecutorConfiguration mongockConfiguration,
                               Map<String, Object> metadata,
                               Function<Parameter, String> parameterNameProvider) {
    super(driver, dependencyManager, mongockConfiguration, metadata, parameterNameProvider);
  }
}
