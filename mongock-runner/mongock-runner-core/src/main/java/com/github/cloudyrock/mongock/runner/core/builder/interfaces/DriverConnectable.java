package com.github.cloudyrock.mongock.runner.core.builder.interfaces;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;

public interface DriverConnectable<BUILDER_TYPE extends DriverConnectable> {
  /**
   * Set the specific connection driver
   * <b>Mandatory</b>
   *
   * @param driver connection driver
   * @return builder for fluent interface
   */
  BUILDER_TYPE setDriver(ConnectionDriver driver);

  /**
   * Indicates that in case the lock cannot be obtained, therefore the migration is not executed, Mongock won't throw
   * any exception and the application will carry on.
   * <p>
   * Only set this to false if the changes are not mandatory and the application can work without them. Leave it true otherwise.
   * <b>Optional</b> Default value true.
   *
   * @return builder for fluent interface
   */
  BUILDER_TYPE dontFailIfCannotAcquireLock();
}
