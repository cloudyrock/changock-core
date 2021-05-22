package com.github.cloudyrock.mongock.runner.core.event;

import java.util.function.Consumer;

public class StandaloneEventPublisher implements EventPublisher {

  private final Runnable migrationStartedListener;
  private final Consumer<MongockMigrationSuccessEvent> migrationSuccessListener;
  private final Consumer<MongockMigrationFailureEvent> migrationFailedListener;

  public StandaloneEventPublisher(Runnable migrationStartedListener,
                                  Consumer<MongockMigrationSuccessEvent> migrationSuccessListener,
                                  Consumer<MongockMigrationFailureEvent> migrationFailedListener) {
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
      migrationSuccessListener.accept(new MongockMigrationSuccessEvent(migrationResult));
    }
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    if(migrationFailedListener != null) {
      migrationFailedListener.accept(new MongockMigrationFailureEvent(ex));
    }
  }

  public Runnable getMigrationStartedListener() {
    return migrationStartedListener;
  }

  public Consumer<MongockMigrationSuccessEvent> getMigrationSuccessListener() {
    return migrationSuccessListener;
  }

  public Consumer<MongockMigrationFailureEvent> getMigrationFailedListener() {
    return migrationFailedListener;
  }
}
