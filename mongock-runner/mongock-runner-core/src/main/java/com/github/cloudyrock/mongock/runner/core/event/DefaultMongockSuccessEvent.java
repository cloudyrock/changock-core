package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;

public class DefaultMongockSuccessEvent implements MongockEvent {

  private final MigrationResult migrationResult;

  DefaultMongockSuccessEvent(MigrationResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}
