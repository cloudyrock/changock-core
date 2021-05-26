package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationFailedResult;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;

public class MigrationFailureEvent implements MongockResultEvent {

  private final MigrationFailedResult migrationResult;

  public MigrationFailureEvent(Exception exception) {

    this.migrationResult = MigrationResult.failedResult(exception);
  }

  public Exception getException() {
    return migrationResult.getException();
  }

  @Override
  public MigrationFailedResult getMigrationResult() {
    return migrationResult;
  }
}
