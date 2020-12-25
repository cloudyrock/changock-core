package io.changock.runner.core.executor;

import io.changock.migration.api.exception.ChangockException;

public class DependencyNotFound extends ChangockException {
  public DependencyNotFound(Exception exception) {
    super(exception);
  }

  public DependencyNotFound(String message) {
    super(message);
  }
}
