package com.github.cloudyrock.mongock.runner.core.changelogs.premigration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogStandard {

  @ChangeSet(author = "executor", id = "standard1", order = "1")
  public void standard1() {
    // Do nothing
  }
  
  @ChangeSet(author = "executor", id = "standard2", order = "2")
  public void standard2() {
    // Do nothing
  }
}
