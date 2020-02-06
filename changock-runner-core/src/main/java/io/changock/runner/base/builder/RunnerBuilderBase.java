package io.changock.runner.base.builder;

import io.changock.driver.api.common.Validable;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.base.ChangeLogService;
import io.changock.runner.base.DependencyManager;
import io.changock.runner.base.MigrationExecutor;
import io.changock.runner.base.ChangockBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;


public abstract class RunnerBuilderBase<BUILDER_TYPE extends RunnerBuilderBase, CHANGOCK_TYPE extends ChangockBase>
    implements DriverBuilderConfigurable<BUILDER_TYPE>, RunnerBuilderConfigurable<BUILDER_TYPE>, Validable {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilderBase.class);
  //Mandatory
  protected String changeLogsScanPackage;
  protected long lockAcquiredForMinutes = 3L;
  protected long maxWaitingForLockMinutes = 4L;
  protected int maxTries = 3;
  protected boolean throwExceptionIfCannotObtainLock = false;
  protected boolean enabled = true;
  protected String startSystemVersion = "0";
  protected String endSystemVersion = String.valueOf(Integer.MAX_VALUE);
  protected Map<String, Object> metadata;
  protected ConnectionDriver driver;

  protected RunnerBuilderBase(){}

  /**
   * Set the specific connection driver
   * <b>Mandatory</b>
   *
   * @param driver connection driver
   * @return builder for fluent interface
   */
  public BUILDER_TYPE setDriver(ConnectionDriver driver) {
    this.driver = driver;
    return returnInstance();
  }

  /**
   * Add a changeLog package to be scanned.
   * <b>Mandatory</b>
   *
   * @param changeLogsScanPackage  package to be scanned
   * @return builder for fluent interface
   */
  public BUILDER_TYPE addChangeLogsScanPackage(String changeLogsScanPackage) {
    this.changeLogsScanPackage = changeLogsScanPackage;
    return returnInstance();
  }

  /**
   * Feature which enables/disables throwing MongockException if the lock cannot be obtained
   * <b>Optional</b> Default value false.
   *
   * @param throwExceptionIfCannotObtainLock Mongock will throw MongockException if lock can not be obtained
   * @return builder for fluent interface
   */
  public BUILDER_TYPE setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    return returnInstance();
  }


  /**
   * Feature which enables/disables execution
   * <b>Optional</b> Default value true.
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return builder for fluent interface
   */
  public BUILDER_TYPE setEnabled(boolean enabled) {
    this.enabled = enabled;
    return returnInstance();
  }

  /**
   * Set up the lock with minimal configuration. This implies Mongock will throw an exception if cannot obtains the lock.
   * <b>Optional</b> Disabled by default.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return builder for fluent interface
   */
  public BUILDER_TYPE setLockConfig(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
    this.throwExceptionIfCannotObtainLock = true;
    return returnInstance();
  }

  /**
   * Set up the lock with default configuration to wait for it and through an exception when cannot obtain it.
   * Default configuration is: lock acquired for 3 minutes, during 4 minutes and 3 max re-tries.
   * <b>Optional</b> Disabled by default.
   *
   * @return builder for fluent interface
   */
  public BUILDER_TYPE setDefaultLock() {
    this.throwExceptionIfCannotObtainLock = true;
    return returnInstance();
  }

  /**
   * Set up the start Version for versioned schema changes.
   * This shouldn't be confused with a supposed change version(Notice, currently changeSet doesn't have version).
   * This is from a consultancy point of view. So the changeSet are tagged with a systemVersion and then when building
   * Mongock, you specify the systemVersion range you want to apply, so all the changeSets tagged with systemVersion
   * inside that range will be applied
   * <b>Optional</b> Default value 0
   *
   * @param startSystemVersion Version to start with
   * @return builder for fluent interface
   */
  public BUILDER_TYPE setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
    return returnInstance();
  }

  /**
   * Set up the end Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagged with systemVersion inside that
   * range will be applied.
   * <b>Optional</b> Default value string value of MAX_INTEGER
   *
   * @param endSystemVersion Version to end with
   * @return builder for fluent interface
   */
  public BUILDER_TYPE setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
    return returnInstance();
  }

  /**
   * Set the metadata for the mongock process. This metadata will be added to each document in the mongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   * <b>Optional</b> Default value empty Map
   *
   * @param metadata Custom metadata object  to be added
   * @return builder for fluent interface
   */
  public BUILDER_TYPE withMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return returnInstance();
  }



  protected final MigrationExecutor buildExecutorDefault() {
    return new MigrationExecutor(
        driver,
        new DependencyManager(),
        lockAcquiredForMinutes,
        maxTries,
        maxWaitingForLockMinutes,
        metadata
    );
  }
  protected final ChangeLogService buildChangeLogServiceDefault() {
    return new ChangeLogService(
        Collections.singletonList(changeLogsScanPackage),
        startSystemVersion,
        endSystemVersion
    );
  }

  @Override
  public void runValidation() throws ChangockException {
    if(driver == null) {
      throw new ChangockException("Driver must be injected to Changock builder");
    }
    if(changeLogsScanPackage == null || "".equals(changeLogsScanPackage)) {
      throw new ChangockException("changeLogsScanPackage must be injected to Changock builder");
    }
    if(!throwExceptionIfCannotObtainLock) {
      logger.warn("throwExceptionIfCannotObtainLock is disabled, which means Changock will continue even if it's not able to acquire the lock");
    }
    if(!"0".equals(startSystemVersion) || !String.valueOf(Integer.MAX_VALUE).equals(endSystemVersion)) {
      logger.info("Running Changock with startSystemVersion[{}] and endSystemVersion[{}]", startSystemVersion, endSystemVersion);
    }
    if(metadata == null){
      logger.info("Running Changock with NO metadata");
    } else {
      logger.info("Running Changock with metadata");
    }
  }

  protected abstract BUILDER_TYPE returnInstance();
  public abstract CHANGOCK_TYPE build();


}
