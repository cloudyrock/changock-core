package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;

public final class ChangockSpring5 {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, MongockSpringConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends ChangockSpringBuilderBase<Builder, ConnectionDriver, MongockSpringConfiguration> {

    private Builder() {
    }



    @Override
    protected Builder returnInstance() {
      return this;
    }

  }

}
