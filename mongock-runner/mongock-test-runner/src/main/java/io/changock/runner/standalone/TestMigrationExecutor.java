package io.changock.runner.standalone;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.executor.DependencyManager;
import io.changock.runner.core.executor.MigrationExecutor;
import io.changock.runner.core.executor.MigrationExecutorConfiguration;

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
