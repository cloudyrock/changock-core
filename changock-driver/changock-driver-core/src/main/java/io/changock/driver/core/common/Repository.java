package io.changock.driver.core.common;

import io.changock.utils.Process;

public interface Repository<DOMAIN_CLASS, ENTITY_CLASS>  extends Process {

  /**
   * Transform a domain object to its persistence representation
   * @param domain domain object that requires to be persisted
   * @return persistence representation of the domain object
   */
  ENTITY_CLASS toEntity(DOMAIN_CLASS domain);

}
