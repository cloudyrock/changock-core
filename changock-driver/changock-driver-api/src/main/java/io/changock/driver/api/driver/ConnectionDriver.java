package io.changock.driver.api.driver;

import io.changock.driver.api.common.Validable;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;

import java.util.Set;

public interface ConnectionDriver extends Validable {
  void setLockSettings(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries);
  void initialize();
  LockManager getLockManager();
  ChangeEntryService getChangeEntryService();
  Set<ChangeSetDependency> getDependencies();
}
