package com.github.cloudyrock.mongock.config;

import com.github.cloudyrock.mongock.exception.MongockException;

import java.util.List;
import java.util.Map;

public class MongockConfiguration {
  private final static String LEGACY_DEFAULT_CHANGELOG_REPOSITORY_NAME = "mongockChangeLog";
  private final static String LEGACY_DEFAULT_LOCK_REPOSITORY_NAME = "mongockLock";
  private static final String PROPERTY_ERROR_TEMPLATE = "Property %s has been removed. Uses properties lockAcquiredForMillis, lockQuitTryingAfterMillis and lockTryFrequencyMillis instead";

  /**
   * Repository name for changeLogs history
   */
  private String changeLogRepositoryName;

  /**
   * If false, Mongock won't create the necessary index. However it will check that they are already
   * created, failing otherwise. Default true
   */
  private boolean indexCreation = true;

  /**
   * Repository name for locking mechanism
   */
  private String lockRepositoryName;

  /**
   * How long the lock will be hold once acquired in minutes. Default 3
   */
  private long lockAcquiredForMillis = 60 * 1000L;

  /**
   * Max time in minutes to wait for the lock in each try. Default 4
   */
  private long lockQuitTryingAfterMillis = 3 * 60 * 1000L;

  /**
   * Max number of times Mongock will try to acquire the lock. Default 3
   */
  private long lockTryFrequencyMillis = 1000L;

  /**
   * Mongock will throw MongockException if lock can not be obtained. Default true
   */
  private boolean throwExceptionIfCannotObtainLock = true;

  /**
   * If true, will track ignored changeSets in history. Default false
   */
  private boolean trackIgnored = false;

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
   * Service identifier.
   */
  private String serviceIdentifier = null;

  /**
   * Map for custom data you want to attach to your migration
   */
  private Map<String, Object> metadata;

  /**
   * When transaction mechanism s possible, ff false, disable transactions. Default true.
   */
  private boolean transactionEnabled = true;

  private LegacyMigration legacyMigration = null;


  public MongockConfiguration() {
    setChangeLogRepositoryName(getChangeLogRepositoryNameDefault());
    setLockRepositoryName(getLockRepositoryNameDefault());
  }


  public long getLockAcquiredForMillis() {
    return lockAcquiredForMillis;
  }

  public void setLockAcquiredForMillis(long lockAcquiredForMillis) {
    this.lockAcquiredForMillis = lockAcquiredForMillis;
  }

  public long getLockQuitTryingAfterMillis() {
    return lockQuitTryingAfterMillis;
  }

  public void setLockQuitTryingAfterMillis(long lockQuitTryingAfterMillis) {
    this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
  }

  public long getLockTryFrequencyMillis() {
    return lockTryFrequencyMillis;
  }

  public void setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
    this.lockTryFrequencyMillis = lockTryFrequencyMillis;
  }

  public String getChangeLogRepositoryName() {
    return changeLogRepositoryName;
  }

  public void setChangeLogRepositoryName(String changeLogRepositoryName) {
    this.changeLogRepositoryName = changeLogRepositoryName;
  }

  public String getLockRepositoryName() {
    return lockRepositoryName;
  }

  public void setLockRepositoryName(String lockRepositoryName) {
    this.lockRepositoryName = lockRepositoryName;
  }

  public boolean isIndexCreation() {
    return indexCreation;
  }

  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
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

  public String getServiceIdentifier() {
    return this.serviceIdentifier;
  }

  public void setServiceIdentifier(String serviceIdentifier) {
    this.serviceIdentifier = serviceIdentifier;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public boolean isTransactionEnabled() {
    return transactionEnabled;
  }

  public void setTransactionEnabled(boolean transactionEnabled) {
    this.transactionEnabled = transactionEnabled;
  }

  public LegacyMigration getLegacyMigration() {
    return legacyMigration;
  }

  public void setLegacyMigration(LegacyMigration legacyMigration) {
    this.legacyMigration = legacyMigration;
  }

  protected String getChangeLogRepositoryNameDefault() {
    return LEGACY_DEFAULT_CHANGELOG_REPOSITORY_NAME;
  }

  protected String getLockRepositoryNameDefault() {
    return LEGACY_DEFAULT_LOCK_REPOSITORY_NAME;
  }


  //TODO remove this legacy methods

  /**
   * //   * @see MongockConfiguration#getChangeLogRepositoryName()
   */
  @Deprecated
  public String getChangeLogCollectionName() {
    return changeLogRepositoryName;
  }

  /**
   * @see MongockConfiguration#setChangeLogRepositoryName(String)
   */
  @Deprecated
  public void setChangeLogCollectionName(String changeLogRepositoryName) {
    setChangeLogRepositoryName(changeLogRepositoryName);
  }

  @Deprecated
  public int getLockAcquiredForMinutes() {
    throw new MongockException(String.format(PROPERTY_ERROR_TEMPLATE, "lockAcquiredForMinutes"));
  }

  @Deprecated
  public void setLockAcquiredForMinutes(int lockAcquiredForMinutes) {
    throw new MongockException(String.format(PROPERTY_ERROR_TEMPLATE, "lockAcquiredForMinutes"));
  }

  @Deprecated
  public int getMaxWaitingForLockMinutes() {
    throw new MongockException(String.format(PROPERTY_ERROR_TEMPLATE, "maxWaitingForLockMinutes"));
  }

  @Deprecated
  public void setMaxWaitingForLockMinutes(int maxWaitingForLockMinutes) {
    throw new MongockException(String.format(PROPERTY_ERROR_TEMPLATE, "maxWaitingForLockMinutes"));
  }

  @Deprecated
  public int getMaxTries() {
    throw new MongockException(String.format(PROPERTY_ERROR_TEMPLATE, "maxTries"));
  }

  @Deprecated
  public void setMaxTries(int maxTries) {
    throw new MongockException(String.format(PROPERTY_ERROR_TEMPLATE, "maxTries"));
  }
}
