package io.changock.driver.core.common;

import io.changock.utils.Process;
import io.changock.utils.field.FieldInstance;
import io.changock.utils.field.FieldUtil;

import java.util.List;
import java.util.stream.Collectors;

public interface Repository<DOMAIN_CLASS, ENTITY_CLASS>  extends Process {

  /**
   * Transform a domain object to its persistence representation
   * @param domain domain object that requires to be persisted
   * @return persistence representation of the domain object
   */
  default ENTITY_CLASS toEntity(DOMAIN_CLASS domain) {
    return mapFieldInstances(
        FieldUtil.getAllFields(domain.getClass())
            .stream()
            .map(field -> new FieldInstance(field, domain))
            .collect(Collectors.toList())
    );
  }

  ENTITY_CLASS mapFieldInstances(List<FieldInstance> fieldInstanceList);

}
