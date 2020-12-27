package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerBase;
import com.github.cloudyrock.spring.util.SpringEventPublisher;
import com.github.cloudyrock.spring.v5.core.ProfiledChangeLogService;
import com.github.cloudyrock.spring.v5.core.SpringMigrationExecutor;
import org.springframework.beans.factory.InitializingBean;

public class SpringInitializingBeanRunner extends MongockRunnerBase implements InitializingBean {

  protected SpringInitializingBeanRunner(SpringMigrationExecutor executor,
                                         ProfiledChangeLogService changeLogService,
                                         boolean throwExceptionIfCannotObtainLock,
                                         boolean enabled,
                                         SpringEventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }

  @Override
  public void afterPropertiesSet() {
    execute();
  }
}
