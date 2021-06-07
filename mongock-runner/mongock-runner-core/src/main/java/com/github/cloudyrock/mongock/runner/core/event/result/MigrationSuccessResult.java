package com.github.cloudyrock.mongock.runner.core.event.result;

public class MigrationSuccessResult<T> extends MigrationResult {

  private final T result;

  public MigrationSuccessResult(T result) {
    super(true);
    this.result = result;
  }

  public T getResult() {
    return result;
  }
}
