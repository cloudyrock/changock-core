package com.github.cloudyrock.mongock.runner.core.changelogs.pretransaction;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.PreTransaction;

@PreTransaction
@ChangeLog(order = "5")
public class ChangeLogPreTransaction {

  @ChangeSet(author = "executor", id = "preTransaction1", order = "1")
  public void preTransaction1() {
  }
  
  @ChangeSet(author = "executor", id = "preTransaction2", order = "2")
  public void preTransaction2() {
  }
}
