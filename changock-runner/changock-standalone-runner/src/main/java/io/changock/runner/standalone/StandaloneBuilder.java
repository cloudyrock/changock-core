package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.builder.RunnerBuilderBase;
import io.changock.runner.core.builder.configuration.ChangockConfiguration;

import java.util.function.Consumer;

public abstract class StandaloneBuilder<BUILDER extends StandaloneBuilder, DRIVER extends ConnectionDriver>
    extends RunnerBuilderBase<BUILDER, DRIVER, ChangockConfiguration> {

  protected Runnable migrationSuccessListener;
  protected Consumer<Exception> migrationFailedListener;

  protected StandaloneBuilder() {
  }

  public BUILDER setMigrationSuccessListener(Runnable listener) {
    this.migrationSuccessListener = listener;
    return returnInstance();
  }

  public BUILDER setMigrationFailListener(Consumer<Exception> listener) {
    this.migrationFailedListener = listener;
    return returnInstance();
  }

  protected StandaloneEventPublisher getEventPublisher() {
    return new StandaloneEventPublisher(migrationSuccessListener, migrationFailedListener);
  }


}
