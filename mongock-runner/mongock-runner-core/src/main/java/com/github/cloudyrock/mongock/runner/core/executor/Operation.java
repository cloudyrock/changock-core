package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.runner.core.executor.change.ChangeExecutorBase;

import java.util.Objects;

public abstract class Operation<T> {

  private final String id;

  public Operation(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Operation)) return false;
    Operation<?> operation = (Operation<?>) o;
    return id.equals(operation.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
