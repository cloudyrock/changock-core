package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.spring.v5.config.ChangockSpring5Configuration;

public final class ChangockSpring5 {


  public static DriverBuilderConfigurable<ChangockSpring5RunnerBuilder, ConnectionDriver> builder() {
    return new ChangockSpring5RunnerBuilder();
  }

  public static class ChangockSpring5RunnerBuilder extends ChangockSpringBuilderBase<ChangockSpring5RunnerBuilder, ConnectionDriver> {


    private ChangockSpring5RunnerBuilder() {
    }

    public ChangockSpring5RunnerBuilder setConfig(ChangockSpring5Configuration config) {
      return this
          .addChangeLogsScanPackage(config.getChangeLogsScanPackage())
          .setLockConfig(config.getLockAcquiredForMinutes(), config.getMaxWaitingForLockMinutes(), config.getMaxTries())//optional
          .setThrowExceptionIfCannotObtainLock(config.isThrowExceptionIfCannotObtainLock())
          .setEnabled(config.isEnabled())
          .setStartSystemVersion(config.getStartSystemVersion())
          .setEndSystemVersion(config.getEndSystemVersion())
          .withMetadata(config.getMetadata());
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
