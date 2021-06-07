package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationSuccessResult;

public class MigrationSuccessEvent<R> implements MongockResultEvent {

  private final MigrationSuccessResult<R> migrationResult;

  public MigrationSuccessEvent(MigrationSuccessResult<R> migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}
