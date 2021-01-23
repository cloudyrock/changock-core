package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.DriverBuilderConfigurable;

public final class MongockStandalone {


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
