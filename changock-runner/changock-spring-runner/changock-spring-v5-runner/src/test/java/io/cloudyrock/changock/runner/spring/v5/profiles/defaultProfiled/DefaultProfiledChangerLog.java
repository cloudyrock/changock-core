package io.cloudyrock.changock.runner.spring.v5.profiles.defaultProfiled;


import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import org.springframework.context.annotation.Profile;


@ChangeLog(order = "01")
@Profile("default")
public class DefaultProfiledChangerLog {

  @Profile("default")
  @ChangeSet(author = "testuser", id = "default-profiled", order = "01")
  public void defaultProfiled() {
    System.out.println("invoked Pdev1"); }

  @ChangeSet(author = "testuser", id = "no-profiled", order = "02")
  public void noProfiled() {
    System.out.println("invoked Pdev2");
  }


}
