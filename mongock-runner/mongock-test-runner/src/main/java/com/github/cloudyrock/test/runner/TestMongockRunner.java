package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilder;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorImpl;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;

import javax.inject.Named;
import java.lang.reflect.Parameter;
import java.util.function.Function;

public class TestMongockRunner extends MongockRunner {
  private static final Function<Parameter, String> DEFAULT_PARAM_NAME_PROVIDER = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;

  public static RunnerBuilder<Builder, MongockConfiguration> builder() {
    return new Builder();
  }

  public static TestMongockRunner.Builder testBuilder() {
    return new Builder();
  }

  private TestMongockRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }

  public static class Builder extends RunnerBuilderBase<Builder, MongockConfiguration> {

    private DependencyManager dependencyManager = new DependencyManager();
    private String executionId;

    private Builder() {
    }

    public Builder setExecutionId(String executionId) {
      this.executionId = executionId;
      return this;
    }

    public TestMongockRunner build() {
      return build(buildExecutorForTest(), getChangeLogService(), throwExceptionIfCannotObtainLock, enabled, new DummyEventPublisher());
    }

    protected MigrationExecutor buildExecutorForTest() {
      return new TestMigrationExecutor(
          executionId,
          driver,
          buildDependencyManager(),
          new MigrationExecutorConfiguration(trackIgnored, serviceIdentifier),
          metadata,
          DEFAULT_PARAM_NAME_PROVIDER
      );
    }


    public TestMongockRunner build(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
      return new TestMongockRunner(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }

    @Override
    protected Builder getInstance() {
      return this;
    }
  }
}
