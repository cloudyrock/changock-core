package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilder;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.migration.ExecutorConfiguration;

import javax.inject.Named;
import java.lang.reflect.Parameter;
import java.util.function.Function;

public class TestMongockRunner extends MongockRunner {
  private static final Function<Parameter, String> DEFAULT_PARAM_NAME_PROVIDER = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;

  public static RunnerBuilder<Builder, MongockConfiguration> builder() {
    return new Builder(new ExecutorFactory<>());
  }

  public static TestMongockRunner.Builder testBuilder() {
    return new Builder(new ExecutorFactory<>());
  }

  private TestMongockRunner(Executor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }

  public static class Builder extends RunnerBuilderBase<Builder, MongockConfiguration, ExecutorConfiguration> {

    private DependencyManager dependencyManager = new DependencyManager();
    private String executionId;

    private Builder(ExecutorFactory<ExecutorConfiguration> executorFactory) {
      super(executorFactory);
    }

    public Builder setExecutionId(String executionId) {
      this.executionId = executionId;
      return this;
    }

    public TestMongockRunner build() {
      return build(buildExecutorForTest(), getChangeLogService(), throwExceptionIfCannotObtainLock, enabled, new DummyEventPublisher());
    }

    protected Executor buildExecutorForTest() {
      return new TestMigrationExecutor(
          executionId,
          driver,
          buildDependencyManager(),
          new ExecutorConfiguration(trackIgnored, serviceIdentifier),
          metadata,
          DEFAULT_PARAM_NAME_PROVIDER
      );
    }


    public TestMongockRunner build(Executor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
      return new TestMongockRunner(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }

    @Override
    protected ExecutorConfiguration getExecutorConfiguration() {
      return new ExecutorConfiguration(trackIgnored, serviceIdentifier);
    }

    @Override
    protected Builder getInstance() {
      return this;
    }
  }
}
