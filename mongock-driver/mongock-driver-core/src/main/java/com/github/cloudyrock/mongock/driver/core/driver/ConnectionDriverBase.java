package com.github.cloudyrock.mongock.driver.core.driver;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.core.lock.DefaultLockManager;
import com.github.cloudyrock.mongock.driver.core.lock.LockRepository;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.TimeService;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public abstract class ConnectionDriverBase<CHANGE_ENTRY extends ChangeEntry> implements ConnectionDriver<CHANGE_ENTRY> {

  private static final TimeService TIME_SERVICE = new TimeService();

  //Lock
  private final int lockAcquiredForSeconds;
  private final int lockQuitTryingAfterSeconds;
  private final int lockTryFrequencySeconds;

  private boolean initialized = false;
  private LockManager lockManager = null;
  private String changeLogRepositoryName;
  private String lockRepositoryName;
  private boolean indexCreation = true;


  protected ConnectionDriverBase(int lockAcquiredForSeconds, int lockQuitTryingAfterSeconds, int lockTryFrequencySeconds) {
    this.lockAcquiredForSeconds = lockAcquiredForSeconds;
    this.lockQuitTryingAfterSeconds = lockQuitTryingAfterSeconds;
    this.lockTryFrequencySeconds = lockTryFrequencySeconds;
  }

  @Override
  public final void initialize() {
    if (!initialized) {
      initialized = true;
      LockRepository lockRepository = this.getLockRepository();
      lockRepository.initialize();
      lockManager = new DefaultLockManager(lockRepository, TIME_SERVICE)
          .setLockAcquiredForMillis(TIME_SERVICE.secondsToMillis(lockAcquiredForSeconds))
          .setLockQuitTryingAfterMillis(TIME_SERVICE.secondsToMillis(lockQuitTryingAfterSeconds))
          .setLockTryFrequencyMillis(TIME_SERVICE.secondsToMillis(lockTryFrequencySeconds));
      getChangeEntryService().initialize();
      specificInitialization();
    }
  }

  @Override
  public LockManager getLockManager() {
    if (lockManager == null) {
      throw new MongockException("Internal error: Driver needs to be initialized by the runner");
    }
    return lockManager;
  }

  @Override
  public LockManager getManagerAndAcquireLock() {
    LockManager lockManager = getLockManager();
    lockManager.acquireLockDefault();
    return lockManager;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public int getLockAcquiredForSeconds() {
    return lockAcquiredForSeconds;
  }

  @Override
  public int getLockQuitTryingAfterSeconds() {
    return lockQuitTryingAfterSeconds;
  }

  @Override
  public int getLockTryFrequencySeconds() {
    return lockTryFrequencySeconds;
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
