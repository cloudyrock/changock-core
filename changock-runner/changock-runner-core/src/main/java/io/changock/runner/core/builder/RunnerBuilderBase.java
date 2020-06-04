package io.changock.runner.core.builder;

import io.changock.driver.api.common.Validable;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.AnnotationProcessor;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.ChangeLogService;
import io.changock.runner.core.DependencyManager;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.MigrationExecutorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public abstract class RunnerBuilderBase<BUILDER_TYPE extends RunnerBuilderBase, DRIVER extends ConnectionDriver, CONFIG extends ChangockConfiguration>
    implements
    DriverBuilderConfigurable<BUILDER_TYPE, DRIVER, CONFIG>,
    RunnerBuilderConfigurable<BUILDER_TYPE, CONFIG>, Validable {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilderBase.class);
  //Mandatory
  protected List<String> changeLogsScanPackage  = new ArrayList<>();
  protected long lockAcquiredForMinutes = 3L;
  protected long maxWaitingForLockMinutes = 4L;
  protected int maxTries = 3;
  protected boolean trackIgnored = false;
  protected boolean throwExceptionIfCannotObtainLock = true;
  protected boolean enabled = true;
  protected String startSystemVersion = "0";
  protected String endSystemVersion = String.valueOf(Integer.MAX_VALUE);
  protected Map<String, Object> metadata;
  protected DRIVER driver;
  protected AnnotationProcessor annotationProcessor;

  protected RunnerBuilderBase() {
  }

  /**
   * Set the specific connection driver
   * <b>Mandatory</b>
   *
   * @param driver connection driver
   * @return builder for fluent interface
   */
  @Override
  public BUILDER_TYPE setDriver(DRIVER driver) {
    this.driver = driver;
    return returnInstance();
  }

  /**
   * Adds a package(or class by its full classname) to be scanned  to the list. Mongo allows multiple packages/classes
   * <b>Requires at least one package/class</b>
   *
   * @param changeLogsScanPackage package to be scanned
   * @return builder for fluent interface
   */
  @Override
  public BUILDER_TYPE addChangeLogsScanPackage(String changeLogsScanPackage) {
    this.changeLogsScanPackage.add(changeLogsScanPackage);
    return returnInstance();
  }

  /**
   * Adds a class to be scanned  to the list. Mongo allows multiple packages/classes
   * <b>Requires at least one package/class</b>
   *
   * @param clazz package to be scanned
   * @return builder for fluent interface
   */
  @Override
  public BUILDER_TYPE addChangeLogClass(Class clazz) {
    addChangeLogsScanPackage(clazz.getName());
    return returnInstance();
  }


  /**
   * Adds a list of packages(or classes by its full classname) to be scanned  to the list.
   * Mongo allows multiple packages/classes
   * <b>Requires at least one package/class</b>
   *
   * @param changeLogsScanPackageList package to be scanned
   * @return builder for fluent interface
   */
  @Override
  public BUILDER_TYPE addChangeLogsScanPackages(List<String> changeLogsScanPackageList) {
    changeLogsScanPackageList.forEach(this::addChangeLogsScanPackage);
    return returnInstance();
  }

  /**
   * <p></p>Feature which enables/disables throwing ChangockException if the lock cannot be obtained, so the
   * the application carries on with no issue.
   * Only make this false if the changes are not mandatory and the app can work without them. Leave it true otherwise.
   * <b>Optional</b> Default value true.
   * </p>
   *
   * @param throwExceptionIfCannotObtainLock Changock will throw ChangockException if lock can not be obtained
   * @return builder for fluent interface
   */
  @Override
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
  @Override
  public BUILDER_TYPE setEnabled(boolean enabled) {
    this.enabled = enabled;
    return returnInstance();
  }


  @Override
  public BUILDER_TYPE setTrackIgnored(boolean trackIgnored) {
    this.trackIgnored = trackIgnored;
    return returnInstance();
  }


  /**
   * Set up the lock with minimal configuration. This implies Changock will throw an exception if cannot obtains the lock.
   * <b>Optional</b> Disabled by default.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return builder for fluent interface
   */
  @Override
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
  @Override
  public BUILDER_TYPE setDefaultLock() {
    this.throwExceptionIfCannotObtainLock = true;
    return returnInstance();
  }

  /**
   * Set up the start Version for versioned schema changes.
   * This shouldn't be confused with a supposed change version(Notice, currently changeSet doesn't have version).
   * This is from a consultancy point of view. So the changeSet are tagged with a systemVersion and then when building
   * Changock, you specify the systemVersion range you want to apply, so all the changeSets tagged with systemVersion
   * inside that range will be applied
   * <b>Optional</b> Default value 0
   *
   * @param startSystemVersion Version to start with
   * @return builder for fluent interface
   */
  @Override
  public BUILDER_TYPE setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
    return returnInstance();
  }

  /**
   * Set up the end Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Changock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagged with systemVersion inside that
   * range will be applied.
   * <b>Optional</b> Default value string value of MAX_INTEGER
   *
   * @param endSystemVersion Version to end with
   * @return builder for fluent interface
   */
  @Override
  public BUILDER_TYPE setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
    return returnInstance();
  }

  /**
   * Set the metadata for the Changock process. This metadata will be added to each document in the ChangockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   * <b>Optional</b> Default value empty Map
   *
   * @param metadata Custom metadata object  to be added
   * @return builder for fluent interface
   */
  @Override
  public BUILDER_TYPE withMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE setConfig(CONFIG config) {
    this
        .addChangeLogsScanPackages(config.getChangeLogsScanPackage())
        .setLockConfig(config.getLockAcquiredForMinutes(), config.getMaxWaitingForLockMinutes(), config.getMaxTries())//optional
        .setThrowExceptionIfCannotObtainLock(config.isThrowExceptionIfCannotObtainLock())
        .setTrackIgnored(config.isTrackIgnored())
        .setEnabled(config.isEnabled())
        .setStartSystemVersion(config.getStartSystemVersion())
        .setEndSystemVersion(config.getEndSystemVersion())
        .withMetadata(config.getMetadata());
    return returnInstance();
  }

  public BUILDER_TYPE overrideAnnoatationProcessor(AnnotationProcessor annotationProcessor) {
    this.annotationProcessor = annotationProcessor;
    return returnInstance();
  }


  @SuppressWarnings("unchecked")
  protected MigrationExecutor buildExecutorDefault() {
    return new MigrationExecutor(
        driver,
        new DependencyManager(),
        new MigrationExecutorConfiguration(lockAcquiredForMinutes, maxTries, maxWaitingForLockMinutes, trackIgnored),
        metadata
    );
  }

  protected ChangeLogService buildChangeLogServiceDefault() {
    return new ChangeLogService(
        changeLogsScanPackage,
        startSystemVersion,
        endSystemVersion,
        annotationProcessor// if null, it will take default ChangockAnnotationManager
    );
  }

  @Override
  public void runValidation() throws ChangockException {
    if (driver == null) {
      throw new ChangockException("Driver must be injected to Changock builder");
    }
    if (changeLogsScanPackage == null || changeLogsScanPackage.isEmpty()) {
      throw new ChangockException("changeLogsScanPackage must be injected to Changock builder");
    }
    if (!throwExceptionIfCannotObtainLock) {
      logger.warn("throwExceptionIfCannotObtainLock is disabled, which means Changock will continue even if it's not able to acquire the lock");
    }
    if (!"0".equals(startSystemVersion) || !String.valueOf(Integer.MAX_VALUE).equals(endSystemVersion)) {
      logger.info("Running Changock with startSystemVersion[{}] and endSystemVersion[{}]", startSystemVersion, endSystemVersion);
    }
    if (metadata == null) {
      logger.info("Running Changock with NO metadata");
    } else {
      logger.info("Running Changock with metadata");
    }
  }

  protected abstract BUILDER_TYPE returnInstance();


}
