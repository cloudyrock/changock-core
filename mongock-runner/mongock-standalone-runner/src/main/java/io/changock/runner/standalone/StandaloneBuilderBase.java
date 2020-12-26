package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import io.changock.runner.standalone.event.StandaloneMigrationFailureEvent;
import io.changock.runner.standalone.event.StandaloneMigrationSuccessEvent;
import io.changock.runner.standalone.event.StandaloneEventPublisher;

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
