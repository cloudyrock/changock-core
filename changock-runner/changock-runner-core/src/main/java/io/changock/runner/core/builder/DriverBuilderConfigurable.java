package io.changock.runner.core.builder;

import io.changock.driver.api.driver.ConnectionDriver;

public interface DriverBuilderConfigurable<BUILDER_TYPE extends RunnerBuilderConfigurable, DRIVER extends ConnectionDriver, CONFIG extends ChangockConfiguration> {
  /**
   * Set the specific connection driver
   * <b>Mandatory</b>
   * @param driver connection driver
   * @return builder for fluent interface
   */
  PackageBuilderConfigurable<BUILDER_TYPE, CONFIG> setDriver(DRIVER driver);
}
