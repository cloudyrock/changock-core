package com.github.cloudyrock.mongock.runner.core.executor;

public abstract class Operation {

  private final String id;

  public Operation(String id) {
    this.id = id;
  }

  public final String getId() {
    return id;
  }
}
