package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.migration.MigrationExecutorImpl;
import com.github.cloudyrock.mongock.runner.core.executor.migration.ExecutorConfiguration;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;

@NotThreadSafe
public class TestMigrationExecutor extends MigrationExecutorImpl {

  private final String executionId;

  public TestMigrationExecutor(String executionId,
                               ConnectionDriver driver,
                               DependencyManager dependencyManager,
                               ExecutorConfiguration executorConfiguration,
                               Map<String, Object> metadata,
                               Function<Parameter, String> paramNameExtractor) {
    super(driver, dependencyManager, executorConfiguration, metadata, paramNameExtractor);
    this.executionId = executionId;
  }

  @Override
  protected String generateExecutionId() {
    return executionId != null ? executionId : super.generateExecutionId();
  }


}
