package io.changock.runner.standalone.event;

import io.changock.runner.core.event.ChangockEvent;
import io.changock.runner.core.event.MigrationResult;

public class StandaloneMigrationFailureEvent {

  private final Exception exception;

  StandaloneMigrationFailureEvent(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
