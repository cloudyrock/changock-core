package io.changock.runner.spring.v5;

import io.changock.runner.core.executor.ChangockBase;
import io.changock.runner.spring.util.SpringEventPublisher;
import io.changock.runner.spring.v5.core.ProfiledChangeLogService;
import io.changock.runner.spring.v5.core.SpringMigrationExecutor;
import org.springframework.beans.factory.InitializingBean;

public class SpringInitializingBeanRunner extends ChangockBase implements InitializingBean {

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
