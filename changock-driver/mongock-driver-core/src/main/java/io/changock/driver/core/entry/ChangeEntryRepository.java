package io.changock.driver.core.entry;

import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.core.common.Repository;

public interface ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry, ENTITY_CLASS> extends ChangeEntryService<CHANGE_ENTRY>, Repository<CHANGE_ENTRY, ENTITY_CLASS> {

}
