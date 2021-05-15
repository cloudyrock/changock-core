package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
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

  protected StandaloneBuilderBase() {
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
  // PRIVATE METHODS
  ///////////////////////////////////////////////////

  protected StandaloneEventPublisher getEventPublisher() {
    return new StandaloneEventPublisher(migrationStartedListener, migrationSuccessListener, migrationFailureListener);
  }

  public MongockRunner buildRunner() {
    return new MongockRunner(buildMigrationExecutor(), getChangeLogService(), throwExceptionIfCannotObtainLock, enabled, getEventPublisher());
  }


}
