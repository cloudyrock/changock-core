package io.changock.runner.standalone;

import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.event.MigrationResult;

import java.util.function.Consumer;

public class StandaloneEventPublisher implements EventPublisher {

  private final Runnable migrationSuccessListener;
  private final Consumer<Exception> migrationFailedListener;

  public StandaloneEventPublisher(Runnable migrationSuccessListener, Consumer<Exception> migrationFailedListener) {
    this.migrationSuccessListener = migrationSuccessListener;
    this.migrationFailedListener = migrationFailedListener;
  }

  @Override
  public void publishMigrationSuccessEvent(MigrationResult migrationResult) {
    if(migrationSuccessListener != null) {
      migrationSuccessListener.run();
    }
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    if(migrationFailedListener != null) {
      migrationFailedListener.accept(ex);
    }
  }
}
