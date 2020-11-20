package io.changock.runner.standalone.event;

import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.event.MigrationResult;

import java.util.function.Consumer;

public class StandaloneEventPublisher implements EventPublisher {

  private final Consumer<StandaloneMigrationSuccessEvent> migrationSuccessListener;
  private final Consumer<StandaloneMigrationFailureEvent> migrationFailedListener;

  public StandaloneEventPublisher(Consumer<StandaloneMigrationSuccessEvent> migrationSuccessListener,
                                  Consumer<StandaloneMigrationFailureEvent> migrationFailedListener) {
    this.migrationSuccessListener = migrationSuccessListener;
    this.migrationFailedListener = migrationFailedListener;
  }

  @Override
  public void publishMigrationStarted() {

  }

  @Override
  public void publishMigrationSuccessEvent(MigrationResult migrationResult) {
    if(migrationSuccessListener != null) {
      migrationSuccessListener.accept(new StandaloneMigrationSuccessEvent(migrationResult));
    }
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    if(migrationFailedListener != null) {
      migrationFailedListener.accept(new StandaloneMigrationFailureEvent(ex));
    }
  }
}
