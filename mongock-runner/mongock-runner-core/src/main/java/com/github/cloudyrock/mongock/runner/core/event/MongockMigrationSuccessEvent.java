package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.MongockEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;

public class MongockMigrationSuccessEvent implements MongockEvent {

  private final MigrationResult migrationResult;

  MongockMigrationSuccessEvent(MigrationResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}
