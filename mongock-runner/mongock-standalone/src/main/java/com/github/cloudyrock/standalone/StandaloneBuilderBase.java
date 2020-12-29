package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.standalone.event.StandaloneEventPublisher;
import com.github.cloudyrock.standalone.event.StandaloneMigrationSuccessEvent;
import com.github.cloudyrock.standalone.event.StandaloneMigrationFailureEvent;

import java.util.function.Consumer;

public abstract class StandaloneBuilderBase<BUILDER extends StandaloneBuilderBase, DRIVER extends ConnectionDriver>
    extends RunnerBuilderBase<BUILDER, DRIVER, MongockConfiguration> {

  protected Runnable migrationStartedListener;
  protected Consumer<StandaloneMigrationSuccessEvent> migrationSuccessListener;
  protected Consumer<StandaloneMigrationFailureEvent> migrationFailureListener;

  protected StandaloneBuilderBase() {
  }


  public BUILDER setMigrationStartedListener(Runnable migrationStartedListener) {
    this.migrationStartedListener = migrationStartedListener;
    return returnInstance();
  }

  public BUILDER setMigrationSuccessListener(Consumer<StandaloneMigrationSuccessEvent> listener) {
    this.migrationSuccessListener = listener;
    return returnInstance();
  }

  public BUILDER setMigrationFailureListener(Consumer<StandaloneMigrationFailureEvent> migrationFailureListener) {
    this.migrationFailureListener = migrationFailureListener;
    return returnInstance();
  }

  protected StandaloneEventPublisher getEventPublisher() {
    return new StandaloneEventPublisher(migrationStartedListener, migrationSuccessListener, migrationFailureListener);
  }

  public StandaloneRunner buildRunner() {
    return new StandaloneRunner(buildExecutorDefault(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled, getEventPublisher());
  }

}
