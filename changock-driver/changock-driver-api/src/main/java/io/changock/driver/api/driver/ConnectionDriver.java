package io.changock.driver.api.driver;

import io.changock.driver.api.common.Validable;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;

import java.util.Set;

public interface ConnectionDriver<CHANGE_ENTRY extends ChangeEntry> extends Validable {
  void setLockSettings(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries);
  void initialize();
  LockManager getLockManager();
  ChangeEntryService<CHANGE_ENTRY> getChangeEntryService();
  Set<ChangeSetDependency> getDependencies();
  ForbiddenParametersMap getForbiddenParameters();
}
