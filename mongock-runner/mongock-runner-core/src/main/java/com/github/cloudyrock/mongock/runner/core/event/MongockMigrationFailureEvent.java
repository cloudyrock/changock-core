package com.github.cloudyrock.mongock.runner.core.event;

public class MongockMigrationFailureEvent {

  private final Exception exception;

  MongockMigrationFailureEvent(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
