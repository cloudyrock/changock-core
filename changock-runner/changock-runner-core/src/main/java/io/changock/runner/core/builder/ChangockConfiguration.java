package io.changock.runner.core.builder;

import java.util.List;
import java.util.Map;

public abstract class ChangockConfiguration {

  /**
   * How long the lock will be hold once acquired in minutes. Default 3
   */
  private int lockAcquiredForMinutes = 3;

  /**
   * Max time in minutes to wait for the lock in each try. Default 4
   */
  private int maxWaitingForLockMinutes = 4;

  /**
   * Max number of times Mongock will try to acquire the lock. Default 3
   */
  private int maxTries = 3;

  /**
   * If true, will track ignored changeSets in history. Default false
   */
  private boolean trackIgnored = false;

  /**
   * Mongock will throw MongockException if lock can not be obtained. Default true
   */
  private boolean throwExceptionIfCannotObtainLock = true;

  /**
   * If false, will disable Mongock. Default true
   */
  private boolean enabled = true;

  /**
   * Package paths where the changeLogs are located. mandatory
   */
  private List<String> changeLogsScanPackage;

  /**
   * System version to start with. Default '0'
   */
  private String startSystemVersion = "0";

  /**
   * System version to end with. Default Integer.MAX_VALUE
   */
  private String endSystemVersion = String.valueOf(Integer.MAX_VALUE);

  /**
   * Map for custom data you want to attach to your migration
   */
  private Map<String, Object> metadata;


  public int getLockAcquiredForMinutes() {
    return lockAcquiredForMinutes;
  }

  public void setLockAcquiredForMinutes(int lockAcquiredForMinutes) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
  }

  public int getMaxWaitingForLockMinutes() {
    return maxWaitingForLockMinutes;
  }

  public void setMaxWaitingForLockMinutes(int maxWaitingForLockMinutes) {
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
  }

  public int getMaxTries() {
    return maxTries;
  }

  public void setMaxTries(int maxTries) {
    this.maxTries = maxTries;
  }

  public boolean isTrackIgnored() {
    return trackIgnored;
  }

  public void setTrackIgnored(boolean trackIgnored) {
    this.trackIgnored = trackIgnored;
  }

  public boolean isThrowExceptionIfCannotObtainLock() {
    return throwExceptionIfCannotObtainLock;
  }

  public void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public List<String> getChangeLogsScanPackage() {
    return changeLogsScanPackage;
  }

  public void setChangeLogsScanPackage(List<String> changeLogsScanPackage) {
    this.changeLogsScanPackage = changeLogsScanPackage;
  }

  public String getStartSystemVersion() {
    return startSystemVersion;
  }

  public void setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
  }

  public String getEndSystemVersion() {
    return endSystemVersion;
  }

  public void setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

}
