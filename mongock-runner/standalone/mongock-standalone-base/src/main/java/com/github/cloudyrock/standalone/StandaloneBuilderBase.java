package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.standalone.event.StandaloneEventPublisher;
import com.github.cloudyrock.standalone.event.StandaloneMigrationFailureEvent;
import com.github.cloudyrock.standalone.event.StandaloneMigrationSuccessEvent;

import java.util.function.Consumer;

public abstract class StandaloneBuilderBase<BUILDER_TYPE extends StandaloneBuilderBase, CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, CONFIG> {

  private Runnable migrationStartedListener;
  private Consumer<StandaloneMigrationSuccessEvent> migrationSuccessListener;
  private Consumer<StandaloneMigrationFailureEvent> migrationFailureListener;

  protected StandaloneBuilderBase(ExecutorFactory  executorFactory, CONFIG config) {
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
  // Builder METHODS
  ///////////////////////////////////////////////////
  @Override
  protected void beforeBuildRunner() {
  }

  @Override
  public MongockRunner buildRunner() {
    return super.buildRunner();
  }

  @Override
  protected EventPublisher buildEventPublisher() {
    return new StandaloneEventPublisher(migrationStartedListener, migrationSuccessListener, migrationFailureListener);
  }


}
