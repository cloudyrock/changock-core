package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.builder.DriverBuilderConfigurable;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerBase;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public final class MongockSpringbootV2_2 {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, MongockSpringConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends MongockSpringBuilder<Builder> {

    private Builder() {
    }

    public MongockApplicationRunner buildApplicationRunner() {
      return new MongockApplicationRunner(getRunner());
    }

    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      return new MongockInitializingBeanRunner(getRunner());
    }

    private MongockRunnerBase getRunner() {
      return new MongockRunnerBase(buildExecutorWithEnvironmentDependency(), getChangeLogService(), throwExceptionIfCannotObtainLock, enabled, applicationEventPublisher);
    }

    @Override
    protected Builder getInstance() {
      return this;
    }

  }

  public static class MongockApplicationRunner implements ApplicationRunner {

    private final MongockRunnerBase runner;

    protected MongockApplicationRunner(MongockRunnerBase runner) {
      this.runner = runner;
    }

    @Override
    public void run(ApplicationArguments args) {
      runner.execute();
    }
  }

  public static class MongockInitializingBeanRunner implements InitializingBean {

    private final MongockRunnerBase runner;

    protected MongockInitializingBeanRunner(MongockRunnerBase runner) {
      this.runner = runner;
    }

    @Override
    public void afterPropertiesSet() {
      runner.execute();
    }
  }
}
