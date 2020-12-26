package io.changock.runner.core.executor;

import com.github.cloudyrock.mongock.exception.MongockException;

public class DependencyNotFound extends MongockException {
  public DependencyNotFound(Exception exception) {
    super(exception);
  }

  public DependencyNotFound(String message) {
    super(message);
  }
}
