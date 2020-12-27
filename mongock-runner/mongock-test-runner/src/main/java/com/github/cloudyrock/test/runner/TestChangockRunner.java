package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerBase;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.DriverBuilderConfigurable;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.config.MongockConfiguration;

public class TestChangockRunner extends MongockRunnerBase {

  public static DriverBuilderConfigurable<Builder, ConnectionDriver, MongockConfiguration> builder() {
    return new Builder();
  }

  public static TestChangockRunner.Builder testBuilder() {
    return new Builder();
  }

  private TestChangockRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }

  public static class Builder extends RunnerBuilderBase<Builder, ConnectionDriver, MongockConfiguration> {

    private DependencyManager dependencyManager = new DependencyManager();
    private String executionId;

    private Builder() {
    }

    public Builder setExecutionId(String executionId) {
      this.executionId = executionId;
      return this;
    }

    public TestChangockRunner build() {
      return build(buildExecutorForTest(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled, new DummyEventPublisher());
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
