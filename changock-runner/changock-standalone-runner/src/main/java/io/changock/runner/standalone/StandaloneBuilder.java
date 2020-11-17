package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.builder.RunnerBuilderBase;
import io.changock.runner.core.builder.configuration.ChangockConfiguration;
import io.changock.runner.standalone.event.StandaloneMigrationFailureEvent;
import io.changock.runner.standalone.event.StandaloneMigrationSuccessEvent;
import io.changock.runner.standalone.event.StandaloneEventPublisher;

import java.util.function.Consumer;

public abstract class StandaloneBuilder<BUILDER extends StandaloneBuilder, DRIVER extends ConnectionDriver>
    extends RunnerBuilderBase<BUILDER, DRIVER, ChangockConfiguration> {

  protected Consumer<StandaloneMigrationSuccessEvent> migrationSuccessListener;
  protected Consumer<StandaloneMigrationFailureEvent> migrationFailedListener;

  protected StandaloneBuilder() {
  }

  public BUILDER setMigrationSuccessListener(Consumer<StandaloneMigrationSuccessEvent> listener) {
    this.migrationSuccessListener = listener;
    return returnInstance();
  }

  public BUILDER setMigrationFailListener(Consumer<StandaloneMigrationFailureEvent> listener) {
    this.migrationFailedListener = listener;
    return returnInstance();
  }

  protected StandaloneEventPublisher getEventPublisher() {
    return new StandaloneEventPublisher(migrationSuccessListener, migrationFailedListener);
  }


}
