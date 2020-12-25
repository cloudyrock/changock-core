package io.changock.runner.core.changelogs.skipmigration.withnochangeset;

import io.changock.migration.api.annotations.ChangeLog;

@ChangeLog(order = "1")
public class ChangeLogWithNoChangeSet {

  public void method() {
  }

}
