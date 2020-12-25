package io.changock.runner.core.event;

public class MigrationResult {

  private final boolean success;

  public MigrationResult(boolean success) {
    this.success = success;
  }

  public MigrationResult() {
    this(true);
  }

  public boolean isSuccess() {
    return success;
  }
}
