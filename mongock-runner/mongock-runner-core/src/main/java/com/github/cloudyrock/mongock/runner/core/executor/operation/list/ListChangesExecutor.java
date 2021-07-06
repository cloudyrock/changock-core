package com.github.cloudyrock.mongock.runner.core.executor.operation.list;

import com.github.cloudyrock.mongock.runner.core.executor.Executor;

public class ListChangesExecutor implements Executor<ListChangesResult> {
  @Override
  public ListChangesResult executeMigration() {
    return new ListChangesResult();
  }

  @Override
  public boolean isExecutionInProgress() {
    return false;
  }

  @Override
  public void close() {

  }
}
