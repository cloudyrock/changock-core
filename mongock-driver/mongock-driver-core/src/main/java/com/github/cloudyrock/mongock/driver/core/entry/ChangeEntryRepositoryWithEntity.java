package com.github.cloudyrock.mongock.driver.core.entry;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.core.common.EntityRepository;

public interface ChangeEntryRepositoryWithEntity<CHANGE_ENTRY extends ChangeEntry, ENTITY_CLASS> extends ChangeEntryRepository<CHANGE_ENTRY>, EntityRepository<CHANGE_ENTRY, ENTITY_CLASS> {

}
