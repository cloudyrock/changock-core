package io.changock.driver.core.entry;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import io.changock.driver.core.common.Repository;

public interface ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry, ENTITY_CLASS> extends ChangeEntryService<CHANGE_ENTRY>, Repository<CHANGE_ENTRY, ENTITY_CLASS> {

}
