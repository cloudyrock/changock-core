package com.github.cloudyrock.mongock.driver.core.lock;

import com.github.cloudyrock.mongock.driver.core.common.EntityRepository;

public interface LockRepositoryWithEntity<ENTITY_CLASS> extends LockRepository, EntityRepository<LockEntry, ENTITY_CLASS> {
}
