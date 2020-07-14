package io.changock.driver.api.driver;

import io.changock.driver.api.common.Validable;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;

import java.util.Set;

public interface ConnectionDriver<CHANGE_ENTRY extends ChangeEntry> extends Validable {
  boolean isInitialized();
  void initialize();
  LockManager getLockManager();
  LockManager getAndAcquireLockManager();
  ChangeEntryService<CHANGE_ENTRY> getChangeEntryService();
  Set<ChangeSetDependency> getDependencies();
  ForbiddenParametersMap getForbiddenParameters();
}
