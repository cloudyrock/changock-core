package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.executor.ExecutorConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.change.MigrationOp;
import com.github.cloudyrock.standalone.event.StandaloneEventPublisher;
import com.github.cloudyrock.standalone.event.StandaloneMigrationFailureEvent;
import com.github.cloudyrock.standalone.event.StandaloneMigrationSuccessEvent;

import java.util.function.Consumer;

public abstract class StandaloneBuilderBase<BUILDER_TYPE extends StandaloneBuilderBase, CONFIG extends MongockConfiguration, EXECUTOR_CONFIG extends ExecutorConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, CONFIG, EXECUTOR_CONFIG> {

  private Runnable migrationStartedListener;
  private Consumer<StandaloneMigrationSuccessEvent> migrationSuccessListener;
  private Consumer<StandaloneMigrationFailureEvent> migrationFailureListener;

  protected StandaloneBuilderBase(ExecutorFactory<EXECUTOR_CONFIG> executorFactory, CONFIG config) {
    super(executorFactory, config);
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationStartedListener(Runnable migrationStartedListener) {
    this.migrationStartedListener = migrationStartedListener;
    return getInstance();
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationSuccessListener(Consumer<StandaloneMigrationSuccessEvent> listener) {
    this.migrationSuccessListener = listener;
    return getInstance();
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationFailureListener(Consumer<StandaloneMigrationFailureEvent> migrationFailureListener) {
    this.migrationFailureListener = migrationFailureListener;
    return getInstance();
  }

  ///////////////////////////////////////////////////
  // Build METHODS
  ///////////////////////////////////////////////////


  public MongockRunner<Boolean> buildRunner() {
    return super.buildRunner(new MigrationOp());
  }

  @Override
  protected EventPublisher buildEventPublisher() {
    return new StandaloneEventPublisher(migrationStartedListener, migrationSuccessListener, migrationFailureListener);
  }


}
