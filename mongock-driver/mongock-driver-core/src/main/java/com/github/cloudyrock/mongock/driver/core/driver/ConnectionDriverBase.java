package com.github.cloudyrock.mongock.driver.core.driver;

import com.github.cloudyrock.mongock.driver.core.lock.LockRepository;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.core.lock.DefaultLockManager;
import com.github.cloudyrock.mongock.utils.TimeService;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public abstract class ConnectionDriverBase<CHANGE_ENTRY extends ChangeEntry> implements ConnectionDriver<CHANGE_ENTRY> {

  private boolean initialized = false;
  private LockManager lockManager = null;
  private long lockAcquiredForMinutes;
  private long maxWaitingForLockMinutes;
  private int maxTries;
  private String changeLogRepositoryName;
  private String lockRepositoryName;
  private boolean indexCreation = true;

  public ConnectionDriverBase() {
  }

  public ConnectionDriverBase(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
  }


  @Override
  public final void initialize() {
    if (!initialized) {
      initialized = true;
      TimeService timeService = new TimeService();
      LockRepository lockRepository = this.getLockRepository();
      lockRepository.initialize();
      lockManager = new DefaultLockManager(lockRepository, timeService)
          .setLockAcquiredForMillis(timeService.minutesToMillis(lockAcquiredForMinutes))
          .setLockMaxTries(maxTries)
          .setLockMaxWaitMillis(timeService.minutesToMillis(maxWaitingForLockMinutes));
      getChangeEntryService().initialize();
      specificInitialization();
    }
  }

  @Override
  public LockManager getLockManager() {
    if(lockManager == null) {
      throw new MongockException("Internal error: Driver needs to be initialized by the runner");
    }
    return lockManager;
  }

  @Override
  public LockManager getAndAcquireLockManager() {
    LockManager lockManager = getLockManager();
    lockManager.acquireLockDefault();
    return lockManager;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public long getLockAcquiredForMinutes() {
    return lockAcquiredForMinutes;
  }

  @Override
  public long getMaxWaitingForLockMinutes() {
    return maxWaitingForLockMinutes;
  }

  @Override
  public int getMaxTries() {
    return maxTries;
  }

  @Override
  public String getChangeLogRepositoryName() {
    return changeLogRepositoryName;
  }

  @Override
  public String getLockRepositoryName() {
    return lockRepositoryName;
  }

  @Override
  public boolean isIndexCreation() {
    return indexCreation;
  }

  @Override
  public void setLockAcquiredForMinutes(long lockAcquiredForMinutes) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
  }

  @Override
  public void setMaxWaitingForLockMinutes(long maxWaitingForLockMinutes) {
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
  }

  @Override
  public void setMaxTries(int maxTries) {
    this.maxTries = maxTries;
  }

  @Override
  public void setChangeLogRepositoryName(String changeLogRepositoryName) {
    this.changeLogRepositoryName = changeLogRepositoryName;
  }

  @Override
  public void setLockRepositoryName(String lockRepositoryName) {
    this.lockRepositoryName = lockRepositoryName;
  }

  @Override
  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
  }

  protected abstract LockRepository getLockRepository();

  protected abstract void specificInitialization();

  @Override
  public void runValidation() throws MongockException {

  }
}
