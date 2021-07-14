package com.github.cloudyrock.mongock.driver.api.entry;

import com.github.cloudyrock.mongock.driver.api.common.RepositoryIndexable;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.Process;


public interface ChangeEntryService<CHANGE_ENTRY extends ChangeEntry> extends RepositoryIndexable, Process {

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

  /**
   * If there is already an ChangeEntry the same executionId, id and author, it will be updated. Otherwise,
   * this method will be inserted.
   * @param changeEntry Entry to be inserted
   * @throws MongockException if any i/o exception or already inserted
   */
  void saveOrUpdate(CHANGE_ENTRY changeEntry) throws MongockException;

  void save(CHANGE_ENTRY changeEntry) throws MongockException;



}
