package io.changock.driver.core.driver;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.lock.DefaultLockManager;
import io.changock.driver.core.lock.LockRepository;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.TimeService;


public abstract class ConnectionDriverBase implements ConnectionDriver {

  private LockManager lockManager = null;

  @Override
  public void initialize(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    if (lockManager == null) {
      TimeService timeService = new TimeService();
      lockManager = new DefaultLockManager(this.getLockRepository(), timeService)
          .setLockAcquiredForMillis(timeService.minutesToMillis(lockAcquiredForMinutes))
          .setLockMaxTries(maxTries)
          .setLockMaxWaitMillis(timeService.minutesToMillis(maxWaitingForLockMinutes));
    } else {
      throw new ChangockException("LockManager already initialised");
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
}
