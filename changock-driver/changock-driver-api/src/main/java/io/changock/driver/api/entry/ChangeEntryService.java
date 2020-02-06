package io.changock.driver.api.entry;

import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.Process;


public interface ChangeEntryService extends Process {

  boolean isNewChange(String changeSetId, String author) throws ChangockException;

  void save(ChangeEntry changeEntry) throws ChangockException;


}
