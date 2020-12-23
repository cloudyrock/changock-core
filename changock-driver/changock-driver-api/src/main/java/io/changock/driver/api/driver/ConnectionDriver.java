package io.changock.driver.api.driver;

import io.changock.driver.api.common.Validable;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;

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
