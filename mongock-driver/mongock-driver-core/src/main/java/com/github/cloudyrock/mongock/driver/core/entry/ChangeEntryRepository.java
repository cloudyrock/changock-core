package com.github.cloudyrock.mongock.driver.core.entry;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.core.common.Repository;

public interface ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry, ENTITY_CLASS> extends ChangeEntryService, Repository<CHANGE_ENTRY, ENTITY_CLASS> {

}
