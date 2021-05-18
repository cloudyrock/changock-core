package com.github.cloudyrock.mongock.runner.core.executor.list;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

public class ListExecutor implements Executor<List> {
  @Override
  public List<String> executeMigration(SortedSet<ChangeLogItem> changeLogs) {
    return Arrays.asList("Change-1", "Change-2", "Change-3", "Change-4");
  }

  @Override
  public boolean isExecutionInProgress() {
    return false;
  }
}
