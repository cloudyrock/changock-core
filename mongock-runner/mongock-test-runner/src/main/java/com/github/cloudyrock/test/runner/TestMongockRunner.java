package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.builder.migration.MigrationBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;

import javax.inject.Named;
import java.lang.reflect.Parameter;
import java.util.function.Function;

public class TestMongockRunner extends MongockRunner {
  private static final Function<Parameter, String> DEFAULT_PARAM_NAME_PROVIDER = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;

  public static Builder builder() {
    return new Builder(new ExecutorFactory<>());
  }

  public static Builder testBuilder() {
    return new Builder(new ExecutorFactory<>());
  }

  private TestMongockRunner(Executor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }

  public static class Builder extends RunnerBuilderBase<Builder, Boolean, MongockConfiguration> implements MigrationBuilderBase<Builder, Boolean, MongockConfiguration> {

    private DependencyManager dependencyManager = new DependencyManager();
    private String executionId;

    private Builder(ExecutorFactory<MongockConfiguration> executorFactory) {
      super(new MigrationOp(), executorFactory, new MongockConfiguration());
    }

    public Builder setExecutionId(String executionId) {
      this.executionId = executionId;
      return this;
    }

    public TestMongockRunner build() {
      return build(buildExecutorForTest(), buildChangeLogService(), config.isThrowExceptionIfCannotObtainLock(), config.isEnabled(), new DummyEventPublisher());
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


    public TestMongockRunner build(Executor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
      return new TestMongockRunner(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
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
