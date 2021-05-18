package com.github.cloudyrock.mongock.runner.core.executor.list;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;

import java.util.Arrays;
import java.util.SortedSet;

public class ListExecutor implements Executor {
  @Override
  public void executeMigration(SortedSet<ChangeLogItem> changeLogs) {
    Arrays.asList("Change-1", "Change-2", "Change-3", "Change-4").forEach(System.out::println);
  }

  @Override
  public boolean isExecutionInProgress() {
    return false;
  }
}
