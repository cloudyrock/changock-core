package io.changock.driver.api.driver;

import io.changock.driver.api.common.ForbiddenParameterException;
import io.changock.utils.OrSupplier;

import java.util.HashMap;
import java.util.Optional;

/**
 * Map structure in which the key is the non-allowed parameter in changeSets and the value is the class which should be replaced by
 */
public class ForbiddenParametersMap extends HashMap<Class, Class> {


  public OrSupplier<Optional<Object>> throwExceptionIfPresent(Class forbiddenClass) throws ForbiddenParameterException {
    Class replacement;
    if((replacement = get(forbiddenClass)) != null) {
      throw new ForbiddenParameterException(forbiddenClass, replacement);
    }
    return new OrSupplier<>();
  }

}
