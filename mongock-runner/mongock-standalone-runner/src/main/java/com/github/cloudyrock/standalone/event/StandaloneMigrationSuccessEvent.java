package com.github.cloudyrock.standalone.event;

import com.github.cloudyrock.mongock.runner.core.event.MongockEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;

public class StandaloneMigrationSuccessEvent implements MongockEvent {

  private final MigrationResult migrationResult;

  StandaloneMigrationSuccessEvent(MigrationResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}
