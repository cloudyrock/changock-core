package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerBase;
import com.github.cloudyrock.spring.util.SpringEventPublisher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class MongockApplicationRunner extends MongockRunnerBase implements ApplicationRunner {

  protected MongockApplicationRunner(MigrationExecutor executor,
                                     ChangeLogService changeLogService,
                                     boolean throwExceptionIfCannotObtainLock,
                                     boolean enabled,
                                     SpringEventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }

  @Override
  public void run(ApplicationArguments args) {
    this.execute();
  }
}
