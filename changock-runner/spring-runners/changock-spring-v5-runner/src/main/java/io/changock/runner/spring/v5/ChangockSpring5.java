package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.migration.api.config.ChangockSpringConfiguration;

public final class ChangockSpring5 {


  public static DriverBuilderConfigurable<ChangockSpring5RunnerBuilder, ConnectionDriver, ChangockSpringConfiguration> builder() {
    return new ChangockSpring5RunnerBuilder();
  }

  public static class ChangockSpring5RunnerBuilder extends ChangockSpringBuilderBase<ChangockSpring5RunnerBuilder, ConnectionDriver, ChangockSpringConfiguration> {


    private ChangockSpring5RunnerBuilder() {
    }


    @Override
    protected ChangockSpring5RunnerBuilder returnInstance() {
      return this;
    }

  }

}
