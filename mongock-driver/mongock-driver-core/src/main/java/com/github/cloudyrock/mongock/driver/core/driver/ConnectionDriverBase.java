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

  private static final int DEFAULT_LOCK_TRY_FREQUENCY = 1;
  private static final TimeService TIME_SERVICE = new TimeService();


  private boolean initialized = false;
  private LockManager lockManager = null;
  private int lockAcquiredForSeconds;
  private int lockQuitTryingAfterSeconds;
  private int lockTryFrequencySeconds;
  private String changeLogRepositoryName;
  private String lockRepositoryName;
  private boolean indexCreation = true;

  public ConnectionDriverBase() {
  }

  @Deprecated
  public ConnectionDriverBase(long lockAcquiredForMinutes, long maxWaitingForLockMinutesEachTry, int maxTries) {
    this(
        TIME_SERVICE.minutesLongToSecondsInt(lockAcquiredForMinutes),
        TIME_SERVICE.minutesLongToSecondsInt(maxWaitingForLockMinutesEachTry * maxTries),
        DEFAULT_LOCK_TRY_FREQUENCY
    );
  }
  public ConnectionDriverBase(int lockAcquiredForSeconds, int lockQuitTryingAfterSeconds, int lockTryFrequencySeconds) {
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
  public void setLockAcquiredForSeconds(int lockAcquiredForSeconds) {
    this.lockAcquiredForSeconds = lockAcquiredForSeconds;
  }

  @Override
  public void setLockQuitTryingAfterSeconds(int lockQuitTryingAfterSeconds) {
    this.lockQuitTryingAfterSeconds = lockQuitTryingAfterSeconds;
  }

  @Override
  public void setLockTryFrequencySeconds(int lockTryFrequencySeconds) {
    this.lockTryFrequencySeconds = lockTryFrequencySeconds;
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
