package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.MigrationBuilderBase;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerImpl;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;

import javax.inject.Named;
import java.lang.reflect.Parameter;
import java.util.function.Function;

public class TestMongockRunner extends MongockRunnerImpl {
  private static final Function<Parameter, String> DEFAULT_PARAM_NAME_PROVIDER = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;

  public static Builder builder() {
    return new Builder(new ExecutorFactory<>());
  }

  public static Builder testBuilder() {
    return new Builder(new ExecutorFactory<>());
  }

  private TestMongockRunner(Executor executor, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }

  public static class Builder extends RunnerBuilderBase<Builder, Boolean, ChangeLogItem, MongockConfiguration> implements MigrationBuilderBase<Builder, Boolean, MongockConfiguration> {

    private String executionId;

    private Builder(ExecutorFactory<ChangeLogItem, MongockConfiguration, Boolean> executorFactory) {
      super(new MigrationOp(), executorFactory, new ChangeLogService(), new DependencyManager(), new MongockConfiguration());
    }

    public Builder setExecutionId(String executionId) {
      this.executionId = executionId;
      return this;
    }

    public TestMongockRunner build() {
      return build(buildExecutorForTest(), config.isThrowExceptionIfCannotObtainLock(), config.isEnabled(), EventPublisher.empty());
    }

    protected Executor buildExecutorForTest() {
      return new TestMigrationExecutor(
          executionId,
          driver,
          dependencyManager,
          DEFAULT_PARAM_NAME_PROVIDER,
          config
      );
    }


    public TestMongockRunner build(Executor executor, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
      return new TestMongockRunner(executor, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }

    @Override
    protected void beforeBuildRunner() {

    }

    @Override
    public Builder getInstance() {
      return this;
    }

  }
}
