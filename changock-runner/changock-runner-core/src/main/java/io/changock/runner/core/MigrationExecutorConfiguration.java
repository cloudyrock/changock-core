package io.changock.runner.core;

public class MigrationExecutorConfiguration {

  private final long lockAcquiredForMinutes;
  private final int maxTries;
  private final long maxWaitingForLockMinutes;
  private final boolean trackIgnored;

  public MigrationExecutorConfiguration(long lockAcquiredForMinutes, int maxTries, long maxWaitingForLockMinutes, boolean trackIgnored) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxTries = maxTries;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.trackIgnored = trackIgnored;
  }

  public long getLockAcquiredForMinutes() {
    return lockAcquiredForMinutes;
  }

  public int getMaxTries() {
    return maxTries;
  }

  public long getMaxWaitingForLockMinutes() {
    return maxWaitingForLockMinutes;
  }

  public boolean isTrackIgnored() {
    return trackIgnored;
  }
}
