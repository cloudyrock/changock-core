package io.cloudyrock.changock.runner.spring.v5.profiles.unprofiled;


import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;


@ChangeLog(order = "01")
public class UnprofiledChangerLog {

  @ChangeSet(author = "testuser", id = "no-profiled", order = "01")
  public void noProfiled() {
    System.out.println("invoked Pdev1");
  }

}
