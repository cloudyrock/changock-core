package io.changock.runner.spring.v5;

import io.changock.runner.core.ChangockBase;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class ChangockSpringApplicationRunner extends ChangockBase implements ApplicationRunner {

  ChangockSpringApplicationRunner(SpringMigrationExecutor executor,
                                  ProfiledChangeLogService changeLogService,
                                  boolean throwExceptionIfCannotObtainLock,
                                  boolean enabled) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
  }

  @Override
  public void run(ApplicationArguments args) {
    this.execute();
  }
}
