package com.github.cloudyrock.mongock.runner.core.executor.change;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;

@NotThreadSafe
public class MigrationExecutorImpl extends ChangeExecutorBase<MongockConfiguration> {


  public MigrationExecutorImpl(ConnectionDriver driver,
                               DependencyManager dependencyManager,
                               Map<String, Object> metadata,
                               Function<Parameter, String> parameterNameProvider,
                               MongockConfiguration config) {
    super(driver, dependencyManager, metadata, parameterNameProvider, config);
  }
}
