package io.changock.runner.standalone.event;

import io.changock.runner.core.event.ChangockEvent;
import io.changock.runner.core.event.MigrationResult;

public class StandaloneMigrationSuccessEvent implements ChangockEvent {

  private final MigrationResult migrationResult;

  StandaloneMigrationSuccessEvent(MigrationResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}
