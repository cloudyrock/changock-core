package io.changock.driver.core.entry;

import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.core.common.Repository;

public interface ChangeEntryRepository<ENTITY_CLASS> extends ChangeEntryService, Repository<ChangeEntry, ENTITY_CLASS> {

}
