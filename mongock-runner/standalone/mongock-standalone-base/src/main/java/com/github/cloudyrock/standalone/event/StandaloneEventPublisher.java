package com.github.cloudyrock.standalone.event;

import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;

import java.util.function.Consumer;

public class StandaloneEventPublisher implements EventPublisher {

  private final Runnable migrationStartedListener;
  private final Consumer<StandaloneMigrationSuccessEvent> migrationSuccessListener;
  private final Consumer<StandaloneMigrationFailureEvent> migrationFailedListener;

  public StandaloneEventPublisher(Runnable migrationStartedListener,
                                  Consumer<StandaloneMigrationSuccessEvent> migrationSuccessListener,
                                  Consumer<StandaloneMigrationFailureEvent> migrationFailedListener) {
    this.migrationSuccessListener = migrationSuccessListener;
    this.migrationFailedListener = migrationFailedListener;
    this.migrationStartedListener = migrationStartedListener;
  }

  @Override
  public void publishMigrationStarted() {
    if(migrationStartedListener != null) {
      migrationStartedListener.run();
    }
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

  public Runnable getMigrationStartedListener() {
    return migrationStartedListener;
  }

  public Consumer<StandaloneMigrationSuccessEvent> getMigrationSuccessListener() {
    return migrationSuccessListener;
  }

  public Consumer<StandaloneMigrationFailureEvent> getMigrationFailedListener() {
    return migrationFailedListener;
  }
}
