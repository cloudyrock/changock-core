package io.changock.driver.core.driver;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.lock.DefaultLockManager;
import io.changock.driver.core.lock.LockRepository;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.TimeService;
import io.changock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public abstract class ConnectionDriverBase<CHANGE_ENTRY extends ChangeEntry> implements ConnectionDriver<CHANGE_ENTRY> {

  private boolean initialized = false;
  private LockManager lockManager = null;
  private final long lockAcquiredForMinutes;
  private final long maxWaitingForLockMinutes;
  private final int maxTries;

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
      throw new ChangockException("Internal error: Driver needs to be initialized by the runner");
    }
    return lockManager;
  }

  protected abstract LockRepository getLockRepository();

  protected abstract void specificInitialization();
}
