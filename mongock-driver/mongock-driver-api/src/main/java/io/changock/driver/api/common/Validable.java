package io.changock.driver.api.common;

import io.changock.migration.api.exception.ChangockException;

public interface Validable {

  void runValidation() throws ChangockException;
}
