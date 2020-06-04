package io.changock.runner.core.changelogs.multipackage;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "2")
public class ChangeLogNoPackage {

  @ChangeSet(author = "mongock", order = "2", id = "no_package")
  public void noPackage() {
  }

  @ChangeSet(author = "mongock", order = "2", id = "no_package_2")
  public void noPackage2() {
  }
}
