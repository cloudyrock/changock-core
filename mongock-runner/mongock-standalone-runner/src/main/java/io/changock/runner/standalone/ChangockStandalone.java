package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;

public final class ChangockStandalone {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, MongockConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends StandaloneBuilderBase<Builder, ConnectionDriver> {

    private Builder() {
    }


    @Override
    protected Builder returnInstance() {
      return this;
    }

  }

}
