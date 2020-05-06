package io.changock.driver.api.driver;

import java.util.HashMap;
import java.util.Optional;

/**
 * Map structure in which the key is the non-allowed parameter in changeSets and the value is the class which should be replaced by
 */
public class NotAllowedParameterMap extends HashMap<Class, Class> {

  public Optional<Class> getReplacementIfNotAllowed(Class key) {
    return Optional.ofNullable(get(key));
  }

}
