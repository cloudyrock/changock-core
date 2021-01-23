package com.github.cloudyrock.standalone.event;

public class StandaloneMigrationFailureEvent {

  private final Exception exception;

  StandaloneMigrationFailureEvent(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
