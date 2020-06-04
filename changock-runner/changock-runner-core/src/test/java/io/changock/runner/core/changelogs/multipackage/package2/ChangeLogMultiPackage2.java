package io.changock.runner.core.changelogs.multipackage.package2;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "3")
public class ChangeLogMultiPackage2 {

  @ChangeSet(author = "mongock", order = "3", id = "changeset_package2")
  public void changeSetPackage2() {
  }
}
