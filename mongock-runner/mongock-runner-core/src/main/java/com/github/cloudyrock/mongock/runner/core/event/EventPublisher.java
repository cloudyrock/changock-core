package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationSuccessResult;

import java.util.function.Consumer;

public class EventPublisher<R> {

  private final Runnable migrationStartedListener;
  private final Consumer<MigrationSuccessResult<R>> migrationSuccessListener;
  private final Consumer<Exception> migrationFailedListener;


  public EventPublisher() {
    this(null, null, null);
  }

  public EventPublisher(Runnable migrationStartedListener,
                        Consumer<MigrationSuccessResult<R>> migrationSuccessListener,
                        Consumer<Exception> migrationFailedListener) {
    this.migrationSuccessListener = migrationSuccessListener;
    this.migrationFailedListener = migrationFailedListener;
    this.migrationStartedListener = migrationStartedListener;
  }

  public void publishMigrationStarted() {
    if (migrationStartedListener != null) {
      migrationStartedListener.run();
    }
  }

  public void publishMigrationSuccessEvent(MigrationSuccessResult<R> migrationResult) {
    if (migrationSuccessListener != null) {
      migrationSuccessListener.accept(migrationResult);
    }
  }

  public void publishMigrationFailedEvent(Exception ex) {
    if (migrationFailedListener != null) {
      migrationFailedListener.accept(ex);
    }
  }

  public Runnable getMigrationStartedListener() {
    return migrationStartedListener;
  }

  public Consumer<MigrationSuccessResult<R>> getMigrationSuccessListener() {
    return migrationSuccessListener;
  }

  public Consumer<Exception> getMigrationFailedListener() {
    return migrationFailedListener;
  }
}
