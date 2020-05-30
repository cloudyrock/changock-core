package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.DependencyManager;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.MigrationExecutorConfiguration;

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
