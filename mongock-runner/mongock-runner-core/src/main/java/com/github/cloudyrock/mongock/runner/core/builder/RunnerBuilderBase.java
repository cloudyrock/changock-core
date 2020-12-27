package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;


public abstract class RunnerBuilderBase<BUILDER_TYPE extends RunnerBuilderBase, DRIVER extends ConnectionDriver, CONFIG extends MongockConfiguration>
    implements
    DriverBuilderConfigurable<BUILDER_TYPE, DRIVER, CONFIG>,
    RunnerBuilderConfigurable<BUILDER_TYPE, CONFIG>, Validable {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilderBase.class);

  protected List<String> changeLogsScanPackage = new ArrayList<>();
  protected List<Class<?>> changeLogsScanClasses = new ArrayList<>();
  protected boolean trackIgnored = false;
  protected boolean throwExceptionIfCannotObtainLock = true;
  protected boolean enabled = true;
  protected String startSystemVersion = "0";
  protected String endSystemVersion = String.valueOf(Integer.MAX_VALUE);
  protected Map<String, Object> metadata;
  protected DRIVER driver;
  protected AnnotationProcessor annotationProcessor;
  protected LegacyMigration legacyMigration = null;
  protected Collection<ChangeSetDependency> dependencies = new ArrayList<>();


  protected RunnerBuilderBase() {
  }

  @Override
  public BUILDER_TYPE setDriver(DRIVER driver) {
    this.driver = driver;
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE addChangeLogsScanPackages(List<String> changeLogsScanPackageList) {
    if (changeLogsScanPackageList != null) {
      changeLogsScanPackage.addAll(changeLogsScanPackageList);
    }
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE addChangeLogClasses(List<Class<?>> classes) {
    if (classes != null) {
      changeLogsScanClasses.addAll(classes);
    }
    return returnInstance();
  }


  @Override
  public BUILDER_TYPE setLegacyMigration(LegacyMigration legacyMigration) {
    this.legacyMigration = legacyMigration;
    if (legacyMigration != null) {
      changeLogsScanPackage.add(driver.getLegacyMigrationChangeLogClass(legacyMigration.isRunAlways()).getPackage().getName());
    }
    return returnInstance();
  }

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

  @Override
  public BUILDER_TYPE dontFailIfCannotAcquireLock() {
    this.throwExceptionIfCannotObtainLock = false;
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE withMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE addDependency(String name, Class type, Object instance) {
    dependencies.add(new ChangeSetDependency(name, type, instance));
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE setConfig(CONFIG config) {
    this.addScanItemsFromConfig(config.getChangeLogsScanPackage());
    if (!config.isThrowExceptionIfCannotObtainLock()) {
      this.dontFailIfCannotAcquireLock();
    }
    this
        .setTrackIgnored(config.isTrackIgnored())
        .setEnabled(config.isEnabled())
        .setStartSystemVersion(config.getStartSystemVersion())
        .setEndSystemVersion(config.getEndSystemVersion())
        .withMetadata(config.getMetadata())
        .setLegacyMigration(config.getLegacyMigration());
    return returnInstance();
  }

  private void addScanItemsFromConfig(List<String> changeLogsScanPackage) {
    for (String itemPath : changeLogsScanPackage) {
      try {
        addChangeLogClass(ClassLoader.getSystemClassLoader().loadClass(itemPath));
      } catch (ClassNotFoundException e) {
        addChangeLogsScanPackage(itemPath);
      }
    }
  }

  public BUILDER_TYPE overrideAnnoatationProcessor(AnnotationProcessor annotationProcessor) {
    this.annotationProcessor = annotationProcessor;
    return returnInstance();
  }


  @SuppressWarnings("unchecked")
  protected MigrationExecutor buildExecutorDefault() {
    return new MigrationExecutor(
        driver,
        buildDependencyManager(),
        new MigrationExecutorConfiguration(trackIgnored),
        metadata
    );
  }

  protected DependencyManager buildDependencyManager() {
    DependencyManager dependencyManager = new DependencyManager();
    if (legacyMigration != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, legacyMigration)
      );
    }
    dependencyManager.addStandardDependencies(dependencies);
    return dependencyManager;
  }

  protected ChangeLogService buildChangeLogServiceDefault() {
    return new ChangeLogService(
        changeLogsScanPackage,
        changeLogsScanClasses,
        startSystemVersion,
        endSystemVersion,
        annotationProcessor// if null, it will take default ChangockAnnotationManager
    );
  }

  @Override
  public void runValidation() throws MongockException {
    if (driver == null) {
      throw new MongockException("Driver must be injected to Mongock builder");
    }
    if (changeLogsScanPackage == null || changeLogsScanPackage.isEmpty()) {
      throw new MongockException("changeLogsScanPackage must be injected to Mongock builder");
    }
    if (!throwExceptionIfCannotObtainLock) {
      logger.warn("throwExceptionIfCannotObtainLock is disabled, which means Mongock will continue even if it's not able to acquire the lock");
    }
    if (!"0".equals(startSystemVersion) || !String.valueOf(Integer.MAX_VALUE).equals(endSystemVersion)) {
      logger.info("Running Mongock with startSystemVersion[{}] and endSystemVersion[{}]", startSystemVersion, endSystemVersion);
    }
    if (metadata == null) {
      logger.info("Running Mongock with NO metadata");
    } else {
      logger.info("Running Mongock with metadata");
    }
  }

  protected abstract BUILDER_TYPE returnInstance();


}
