package io.changock.driver.core.lock;

import io.changock.driver.api.lock.LockCheckException;
import io.changock.driver.api.lock.LockManager;
import io.changock.utils.TimeService;
import io.changock.utils.annotation.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

/**
 * <p>This class is responsible of managing the lock at high level. It provides 3 main methods which are
 * for acquiring, ensuring(doesn't acquires the lock, just refresh the expiration time if required) and
 * releasing the lock.</p>
 * <p>Implementation note: This class is not thread safe. If in future development thread safety is needed, please consider
 * using volatile for expiresAt field and synchronized mechanism.</p>
 */
@NotThreadSafe
public class DefaultLockManager implements LockManager {

  //static constants
  private static final Logger logger = LoggerFactory.getLogger(DefaultLockManager.class);
  private static final long MIN_LOCK_ACQUIRED_FOR_MILLIS = 2L * 60L * 1000L; // 2 minutes
  private static final long LOCK_REFRESH_MARGIN = 60L * 1000L;// 1 minute
  private static final long MINIMUM_SLEEP_THREAD = 500L;

  //long debug/info/error messages
  private static final String MAX_WAIT_EXCEEDED_ERROR_MSG =
      "Waiting time required(%d ms) to take the lock is longer than maxWaitingTime(%d ms)";
  private static final String GOING_TO_SLEEP_MSG =
      "Changock is going to sleep to wait for the lock:  {} ms({} minutes)";
  private static final String EXPIRATION_ARG_ERROR_MSG = "Lock expiration period must be greater than %d ms";
  private static final String MAX_TRIES_ERROR_TEMPLATE = "MaxTries(%d) reached due to LockPersistenceException: \n\tcurrent lock:  %s\n\tnew lock: %s\n\tacquireLockQuery: %s\n\tdb error detail: %s";
  private static final String LOCK_HELD_BY_OTHER_PROCESS = "Lock held by other process. Cannot ensure lock.\n\tCurrent try: %d\n\tcurrent lock:  %s\n\tnew lock: %s\n\tacquireLockQuery: %s\n\tdb error detail: %s";


  //injections
  private final LockRepository repository;
  private final TimeService timeUtils;
  /**
   * Owner of the lock
   */
  private final String owner;
  /**
   * <p>Maximum time it will wait for the lock in each try.</p>
   * <p>Default 3 minutes</p>
   */
  private long lockMaxWaitMillis = MIN_LOCK_ACQUIRED_FOR_MILLIS + (60 * 1000);
  /**
   * Maximum number of times it will try to take the lock if it's owned by some one else
   */
  private int lockMaxTries = 1;
  /**
   * <p>The period of time for which the lock will be owned.</p>
   * <p>Default 2 minutes</p>
   */
  private long lockAcquiredForMillis = MIN_LOCK_ACQUIRED_FOR_MILLIS;
  /**
   * Moment when will mandatory to acquire the lock again.
   */
  private Date lockExpiresAt = null;

  /**
   * Number of tries
   */
  private int tries = 0;

  /**
   * Constructor takes some bean injections
   *
   * @param repository lock repository
   * @param timeUtils  time utils service
   */
  public DefaultLockManager(LockRepository repository, TimeService timeUtils) {
    this.repository = repository;
    this.timeUtils = timeUtils;
    this.owner = UUID.randomUUID().toString();//TODO reconsider this
  }

  /**
   * <p>Tries to acquire the default lock regardless who is the current owner.</p>
   * <p>If the lock is already acquired by the current LockChecker or is expired, will be updated</p>
   * <p>In case the lock is acquired by another LockChecker, it will wait until the current lock is expired
   * and will try to acquire it again. This will be repeated as many times as (maxTries - 1)</p>
   *
   * @throws LockCheckException if the lock cannot be acquired
   */
  public void acquireLockDefault() throws LockCheckException {
    acquireLock(getDefaultKey());
  }

  private void acquireLock(String lockKey) {
    boolean keepLooping = true;
    do {
      try {
        logger.info("Changock trying to acquire the lock");
        Date newLockExpiresAt = timeUtils.currentTimePlusMillis(lockAcquiredForMillis);
        repository.insertUpdate(new LockEntry(lockKey, LockStatus.LOCK_HELD.name(), owner, newLockExpiresAt));
        logger.info("Changock acquired the lock until: {}", newLockExpiresAt);
        updateStatus(newLockExpiresAt);
        keepLooping = false;
      } catch (LockPersistenceException ex) {
        handleLockException(true, ex);
      }
    } while (keepLooping);
  }

  /**
   * <p>Tries to refresh the default lock when the current LockChecker has the lock or , when the lock
   * is expired, is the last owner</p>
   * <p>Notice that it does not try to acquire when is acquired by another LockChecker</p>
   *
   * @throws LockCheckException if, in case of needed, the lock cannot be refreshed
   */
  public void ensureLockDefault() throws LockCheckException {
    ensureLock(getDefaultKey());
  }

  private void ensureLock(String lockKey) {
    boolean keepLooping = true;
    do {
      if (needsRefreshLock()) {
        try {
          logger.info("Changock trying to refresh the lock");
          Date lockExpiresAtTemp = timeUtils.currentTimePlusMillis(lockAcquiredForMillis);
          LockEntry lockEntry = new LockEntry(lockKey, LockStatus.LOCK_HELD.name(), owner, lockExpiresAtTemp);
          repository.updateIfSameOwner(lockEntry);
          updateStatus(lockExpiresAtTemp);
          logger.info("Changock refreshed the lock until: {}", lockExpiresAtTemp);
          keepLooping = false;
        } catch (LockPersistenceException ex) {
          handleLockException(false, ex);
        }
      } else {
        keepLooping = false;
      }
    } while (keepLooping);
  }

  /**
   * <p>Release the default lock when is acquired by the current LockChecker.</p>
   * <p>When the lock is not acquired by the current LockChecker, it won't make any change.
   * Does not throw any exception neither.</p>
   * <p>Idempotent operation.</p>
   */
  public void releaseLockDefault() {
    releaseLock(getDefaultKey());
  }

  @Override
  public void close() {
    releaseLockDefault();
  }

  private void releaseLock(String lockKey) {
    logger.info("Changock is trying to release the lock.");
    repository.removeByKeyAndOwner(lockKey, this.getOwner());
    this.lockExpiresAt = null;
    logger.info("Changock released the lock");

  }

  /**
   * <p>If the flag 'waitForLog' is set, indicates the maximum time it will wait for the lock in each try.</p>
   * <p>Default 3 minutes</p>
   *
   * @param lockMaxWaitMillis max waiting time for lock. Must be greater than 0
   * @return LockChecker object for fluent interface
   */
  public DefaultLockManager setLockMaxWaitMillis(long lockMaxWaitMillis) {
    if (lockMaxWaitMillis <= 0) {
      throw new IllegalArgumentException("Lock max wait must be grater than 0 ");
    }
    this.lockMaxWaitMillis = lockMaxWaitMillis;
    return this;
  }

  /**
   * @return max tries
   */
  public int getLockMaxTries() {
    return lockMaxTries;
  }

  /**
   * <p>Updates the maximum number of tries to acquire the lock, if the flag 'waitForLog' is set </p>
   * <p>Default 1</p>
   *
   * @param lockMaxTries number of tries
   * @return LockChecker object for fluent interface
   */
  public DefaultLockManager setLockMaxTries(int lockMaxTries) {
    if (lockMaxTries <= 0) {
      throw new IllegalArgumentException("Lock max tries must be grater than 0 ");
    }
    this.lockMaxTries = lockMaxTries;
    return this;
  }

  /**
   * <p>Indicates the number of milliseconds the lock will be acquired for</p>
   * <p>Default 3 minutes</p>
   *
   * @param lockAcquiredForMillis milliseconds the lock will be acquired for
   * @return LockChecker object for fluent interface
   */
  public DefaultLockManager setLockAcquiredForMillis(long lockAcquiredForMillis) {
    if (lockAcquiredForMillis < MIN_LOCK_ACQUIRED_FOR_MILLIS) {
      throw new IllegalArgumentException(String.format(EXPIRATION_ARG_ERROR_MSG, MIN_LOCK_ACQUIRED_FOR_MILLIS));
    }
    this.lockAcquiredForMillis = lockAcquiredForMillis;
    return this;
  }

  private void handleLockException(boolean acquiringLock, LockPersistenceException ex) {

    this.tries++;

    LockEntry currentLock = repository.findByKey(getDefaultKey());

    if (this.tries >= lockMaxTries) {
      updateStatus(null);
      throw new LockCheckException(String.format(
          MAX_TRIES_ERROR_TEMPLATE,
          lockMaxTries,
          currentLock != null ? currentLock.toString() : "none",
          ex.getNewLockEntity(),
          ex.getAcquireLockQuery(),
          ex.getDbErrorDetail()));
    }


    if (currentLock != null && !currentLock.isOwner(owner)) {
      Date currentLockExpiresAt = currentLock.getExpiresAt();
      logger.warn("Lock is taken by other process until: {}", currentLockExpiresAt);
      if (!acquiringLock) {
        throw new LockCheckException(String.format(
            LOCK_HELD_BY_OTHER_PROCESS,
            this.tries,
            currentLock.toString(),
            ex.getNewLockEntity(),
            ex.getAcquireLockQuery(),
            ex.getDbErrorDetail()));
      }
      waitForLock(currentLockExpiresAt);
    }

  }

  private void waitForLock(Date expiresAtMillis) {
    long diffMillis = expiresAtMillis.getTime() - timeUtils.currentTime().getTime();
    long sleepingMillis = (diffMillis > 0 ? diffMillis : 0) + MINIMUM_SLEEP_THREAD;
    try {
      if (sleepingMillis > lockMaxWaitMillis) {
        throw new LockCheckException(String.format(MAX_WAIT_EXCEEDED_ERROR_MSG, sleepingMillis, lockMaxWaitMillis));
      }
      logger.info(GOING_TO_SLEEP_MSG, sleepingMillis, timeUtils.millisToMinutes(sleepingMillis));
      Thread.sleep(sleepingMillis);
    } catch (InterruptedException ex) {
      logger.error("ERROR acquiring the lock", ex);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public String getOwner() {
    return owner;
  }

  public boolean isLockHeld() {
    return this.lockExpiresAt != null && timeUtils.currentTime().compareTo(lockExpiresAt) < 1;
  }

  private boolean needsRefreshLock() {
    return this.lockExpiresAt == null
        || timeUtils.currentTime().compareTo(new Date(this.lockExpiresAt.getTime() - LOCK_REFRESH_MARGIN)) >= 0;
  }

  private void updateStatus(Date lockExpiresAt) {
    this.lockExpiresAt = lockExpiresAt;
    this.tries = 0;
  }

}
