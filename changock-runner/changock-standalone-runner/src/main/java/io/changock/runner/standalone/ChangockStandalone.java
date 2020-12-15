package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.config.ChangockConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;

public final class ChangockStandalone {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, ChangockConfiguration> builder() {
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
