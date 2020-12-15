package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.migration.api.config.ChangockSpringConfiguration;

public final class ChangockSpring5 {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, ChangockSpringConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends ChangockSpringBuilderBase<Builder, ConnectionDriver, ChangockSpringConfiguration> {

    private Builder() {
    }



    @Override
    protected Builder returnInstance() {
      return this;
    }

  }

}
