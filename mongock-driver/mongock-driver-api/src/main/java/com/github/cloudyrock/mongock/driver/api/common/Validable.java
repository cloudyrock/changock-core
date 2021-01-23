package com.github.cloudyrock.mongock.driver.api.common;

import com.github.cloudyrock.mongock.exception.MongockException;

public interface Validable {

  void runValidation() throws MongockException;
}
