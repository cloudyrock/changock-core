package com.github.cloudyrock.mongock.runner.core.changelogs.posttransaction;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.PostTransaction;

@PostTransaction
@ChangeLog(order = "1")
public class ChangeLogPostTransaction {

  @ChangeSet(author = "executor", id = "postTransaction1", order = "1")
  public void postTransaction1() {
  }
  
  @ChangeSet(author = "executor", id = "postTransaction2", order = "2")
  public void postTransaction2() {
  }
}
