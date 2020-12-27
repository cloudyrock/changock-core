package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;

@NotThreadSafe
public class TestMigrationExecutor extends MigrationExecutor {

  private final String executionId;

  public TestMigrationExecutor(String executionId,
                               ConnectionDriver driver,
                               DependencyManager dependencyManager,
                               MigrationExecutorConfiguration migrationExecutorConfiguration,
                               Map<String, Object> metadata) {
    super(driver, dependencyManager, migrationExecutorConfiguration, metadata);
    this.executionId = executionId;
  }

  protected String generateExecutionId() {
    return executionId != null ? executionId : super.generateExecutionId();
  }


}
