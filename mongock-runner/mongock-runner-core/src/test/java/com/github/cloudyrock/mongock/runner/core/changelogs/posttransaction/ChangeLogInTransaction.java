package com.github.cloudyrock.mongock.runner.core.changelogs.posttransaction;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "2")
public class ChangeLogInTransaction {

  @ChangeSet(author = "executor", id = "inTransaction1", order = "1")
  public void inTransaction1() {
  }
  
  @ChangeSet(author = "executor", id = "inTransaction2", order = "2")
  public void inTransaction2() {
  }
}
