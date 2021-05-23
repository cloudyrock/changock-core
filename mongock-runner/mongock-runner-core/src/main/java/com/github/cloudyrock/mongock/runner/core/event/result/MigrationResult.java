package com.github.cloudyrock.mongock.runner.core.event.result;

public class MigrationResult {

  private final boolean success;

  public static MigrationResult successResult() {
    return new MigrationResult(true);
  }

  public static MigrationFailedResult failedResult(Exception ex) {
    return new MigrationFailedResult(ex);
  }

  protected MigrationResult(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}
