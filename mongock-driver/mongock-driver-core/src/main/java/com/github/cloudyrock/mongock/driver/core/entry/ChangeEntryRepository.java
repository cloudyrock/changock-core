package com.github.cloudyrock.mongock.driver.core.entry;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.core.common.EntityRepository;

public interface ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends ChangeEntryService<CHANGE_ENTRY>{

}
