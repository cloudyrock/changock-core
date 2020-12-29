package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.builder.DriverBuilderConfigurable;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;

public final class MongockSpring5 {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, MongockSpringConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends MongockSpringBuilderBase<Builder, ConnectionDriver, MongockSpringConfiguration> {

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

}
