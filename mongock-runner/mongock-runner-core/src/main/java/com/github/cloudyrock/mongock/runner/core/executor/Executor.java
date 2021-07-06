package com.github.cloudyrock.mongock.runner.core.executor;

import java.io.Closeable;

public interface Executor<T> extends Closeable {
  T executeMigration();

  boolean isExecutionInProgress();
}
