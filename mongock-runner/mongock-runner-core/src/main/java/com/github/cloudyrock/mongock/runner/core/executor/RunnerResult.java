package com.github.cloudyrock.mongock.runner.core.executor;

import java.util.Optional;

public class RunnerResult<T> {

  private final Status status;

  private final T result;

  RunnerResult(T result) {
    this.result = result;
    this.status = Status.OK;
  }

  RunnerResult(Status status) {
    this.status = status;
    result = null;
  }

  public Optional<T> get() {
    return Optional.ofNullable(result);
  }

  public enum Status {OK, DISABLED, ERROR}


}
