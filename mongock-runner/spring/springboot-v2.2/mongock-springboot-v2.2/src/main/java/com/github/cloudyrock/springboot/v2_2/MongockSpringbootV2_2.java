package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.builder.DriverBuilderConfigurable;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerBase;
import com.github.cloudyrock.spring.util.SpringEventPublisher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public final class MongockSpringbootV2_2 {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, MongockSpringConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends MongockSpringBuilderBase<
      Builder,
      MongockApplicationRunner,
      MongockInitializingBeanRunner,
      ConnectionDriver,
      MongockSpringConfiguration> {

    private Builder() {
    }

    public MongockApplicationRunner buildApplicationRunner() {
      return new MongockApplicationRunner(
          buildExecutorWithEnvironmentDependency(),
          buildProfiledChangeLogService(),
          throwExceptionIfCannotObtainLock,
          enabled,
          buildSpringEventPublisher());
    }

    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      return new MongockInitializingBeanRunner(
          buildExecutorWithEnvironmentDependency(),
          buildProfiledChangeLogService(),
          throwExceptionIfCannotObtainLock,
          enabled,
          buildSpringEventPublisher());
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }

  }

  public static class MongockApplicationRunner extends MongockRunnerBase implements ApplicationRunner {

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

  public static class MongockInitializingBeanRunner extends MongockRunnerBase implements InitializingBean {

    protected MongockInitializingBeanRunner(MigrationExecutor executor,
                                            ChangeLogService changeLogService,
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
}
