package com.github.cloudyrock.mongock.driver.api.driver;

import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;

import java.util.Set;

public interface ConnectionDriver<CHANGE_ENTRY extends ChangeEntry> extends Validable {
  void initialize();

  LockManager getLockManager();

  LockManager getAndAcquireLockManager();

  ChangeEntryService<CHANGE_ENTRY> getChangeEntryService();

  Set<ChangeSetDependency> getDependencies();

  ForbiddenParametersMap getForbiddenParameters();

  Class getLegacyMigrationChangeLogClass(boolean runAlways);

  void setLockAcquiredForMinutes(long lockAcquiredForMinutes);

  void setMaxWaitingForLockMinutes(long maxWaitingForLockMinutes);

  void setMaxTries(int maxTries);

  void setChangeLogRepositoryName(String changeLogRepositoryName);

  void setLockRepositoryName(String lockRepositoryName);

  void setIndexCreation(boolean indexCreation);

  boolean isInitialized();

  long getLockAcquiredForMinutes();

  long getMaxWaitingForLockMinutes();

  int getMaxTries();

  String getChangeLogRepositoryName();

  String getLockRepositoryName();

  boolean isIndexCreation();
}
