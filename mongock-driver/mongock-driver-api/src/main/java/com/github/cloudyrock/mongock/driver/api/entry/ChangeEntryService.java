package com.github.cloudyrock.mongock.driver.api.entry;

import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.Process;


public interface ChangeEntryService<CHANGE_ENTRY extends ChangeEntry> extends Process {

  /**
   * <p>Retrieves is a changeSet with given changeSetId and author hasn't been already executed. This means
   * there is no changeSet in the changeLog store for the given changeSetId and author, or its state is not</p>
   * EXECUTED.
   *
   * @param changeSetId changeSet id
   * @param author      changeSet's author
   * @return tru if it has not been executed yet, false otherwise
   * @throws MongockException if anything goes wrong
   */
  boolean isAlreadyExecuted(String changeSetId, String author) throws MongockException;

  void save(CHANGE_ENTRY changeEntry) throws MongockException;


}
