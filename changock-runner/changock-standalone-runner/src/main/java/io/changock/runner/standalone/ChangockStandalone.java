package io.changock.runner.standalone;

import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.config.ChangockConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;

public final class ChangockStandalone {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, ChangockConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends StandaloneBuilderBase<Builder, ConnectionDriver> {

    private Builder() {
      //temporally until mongock runners are fully removed
      overrideAnnoatationProcessor(new MongockAnnotationProcessor());
    }


    @Override
    protected Builder returnInstance() {
      return this;
    }

  }

}
