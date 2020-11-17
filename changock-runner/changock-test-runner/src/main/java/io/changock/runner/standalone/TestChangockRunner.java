package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.executor.ChangeLogService;
import io.changock.runner.core.executor.ChangockBase;
import io.changock.runner.core.executor.DependencyManager;
import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.executor.MigrationExecutor;
import io.changock.runner.core.executor.MigrationExecutorConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.builder.RunnerBuilderBase;
import io.changock.runner.core.builder.configuration.ChangockConfiguration;

public class TestChangockRunner extends ChangockBase {

  public static DriverBuilderConfigurable<Builder, ConnectionDriver, ChangockConfiguration> builder() {
    return new Builder();
  }

  public static TestChangockRunner.Builder testBuilder() {
    return new Builder();
  }

  private TestChangockRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }

  public static class Builder extends RunnerBuilderBase<Builder, ConnectionDriver, ChangockConfiguration> {

    private DependencyManager dependencyManager = new DependencyManager();
    private String executionId;

    private Builder() {
    }

    public Builder setExecutionId(String executionId) {
      this.executionId = executionId;
      return this;
    }

    public TestChangockRunner build() {
      return build(buildExecutorForTest(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled, new TestEventPublisher());
    }

    protected MigrationExecutor buildExecutorForTest() {
      return new TestMigrationExecutor(
          executionId,
          driver,
          buildDependencyManager(),
          new MigrationExecutorConfiguration(trackIgnored),
          metadata
      );
    }


    public TestChangockRunner build(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
      return new TestChangockRunner(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }
  }
}
