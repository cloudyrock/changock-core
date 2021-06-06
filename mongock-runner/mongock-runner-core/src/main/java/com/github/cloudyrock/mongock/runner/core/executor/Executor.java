package com.github.cloudyrock.mongock.runner.core.executor;

public interface Executor<T> {
  T executeMigration();
  boolean isExecutionInProgress();
}
