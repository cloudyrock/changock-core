package io.changock.runner.base.builder;

import io.changock.driver.api.driver.ConnectionDriver;

public interface DriverBuilderConfigurable<BUILDER_TYPE extends RunnerBuilderConfigurable> {
  /**
   * Set the specific connection driver
   * <b>Mandatory</b>
   * @param driver connection driver
   * @return builder for fluent interface
   */
  PackageBuilderConfigurable<BUILDER_TYPE> setDriver(ConnectionDriver driver);
}
