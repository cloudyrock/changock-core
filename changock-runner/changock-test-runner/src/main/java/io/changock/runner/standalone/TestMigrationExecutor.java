package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.DependencyManager;
import io.changock.runner.core.MigrationExecutor;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;

@NotThreadSafe
public class TestMigrationExecutor extends MigrationExecutor {

  private final String executionId;

  public TestMigrationExecutor(String executionId,
                               ConnectionDriver driver,
                               DependencyManager dependencyManager,
                               long lockAcquiredForMinutes,
                               int maxTries,
                               long maxWaitingForLockMinutes,
                               Map<String, Object> metadata) {
    super(driver, dependencyManager, lockAcquiredForMinutes, maxTries, maxWaitingForLockMinutes, metadata);
    this.executionId = executionId;
  }

  protected String generateExecutionId() {
    return executionId;
  }


}
