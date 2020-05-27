package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.spring.util.config.ChangockSpringConfiguration;

public final class ChangockSpring5 {


  public static DriverBuilderConfigurable<ChangockSpring5RunnerBuilder, ConnectionDriver, ChangockSpringConfiguration> builder() {
    return new ChangockSpring5RunnerBuilder();
  }

  public static class ChangockSpring5RunnerBuilder extends ChangockSpringBuilderBase<ChangockSpring5RunnerBuilder, ConnectionDriver, ChangockSpringConfiguration> {


    private ChangockSpring5RunnerBuilder() {
    }

    public ChangockSpringApplicationRunner buildApplicationRunner() {
      return new ChangockSpringApplicationRunner(
          buildExecutorWithEnvironmentDependency(),
          buildProfiledChangeLogService(),
          throwExceptionIfCannotObtainLock,
          enabled);
    }

    public ChangockSpringInitializingBeanRunner buildInitializingBeanRunner() {
      return new ChangockSpringInitializingBeanRunner(
          buildExecutorWithEnvironmentDependency(),
          buildProfiledChangeLogService(),
          throwExceptionIfCannotObtainLock,
          enabled);
    }

    @Override
    protected ChangockSpring5RunnerBuilder returnInstance() {
      return this;
    }

  }

}
