package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;

import java.util.function.Consumer;

public class MongockEventPublisher implements EventPublisher {

  private final Runnable migrationStartedListener;
  private final Consumer<MigrationResult> migrationSuccessListener;
  private final Consumer<Exception> migrationFailedListener;

  public MongockEventPublisher(Runnable migrationStartedListener,
                               Consumer<MigrationResult> migrationSuccessListener,
                               Consumer<Exception> migrationFailedListener) {
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
      migrationSuccessListener.accept(migrationResult);
    }
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    if(migrationFailedListener != null) {
      migrationFailedListener.accept(ex);
    }
  }

  public Runnable getMigrationStartedListener() {
    return migrationStartedListener;
  }

  public Consumer<MigrationResult> getMigrationSuccessListener() {
    return migrationSuccessListener;
  }

  public Consumer<Exception> getMigrationFailedListener() {
    return migrationFailedListener;
  }
}
