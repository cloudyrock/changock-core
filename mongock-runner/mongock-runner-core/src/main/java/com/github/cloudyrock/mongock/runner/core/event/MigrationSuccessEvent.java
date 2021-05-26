package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;

public class MigrationSuccessEvent implements MongockResultEvent {

  private final MigrationResult migrationResult;

  public MigrationSuccessEvent(MigrationResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}
