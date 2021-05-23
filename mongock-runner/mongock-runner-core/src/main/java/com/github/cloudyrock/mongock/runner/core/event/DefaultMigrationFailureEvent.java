package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationFailedResult;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;

public class DefaultMigrationFailureEvent implements MongockEvent {

  private final MigrationFailedResult migrationResult;

  DefaultMigrationFailureEvent(Exception exception) {

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
