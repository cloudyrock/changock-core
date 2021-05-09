package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorImpl;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;


public abstract class RunnerBuilderBase<BUILDER_TYPE extends RunnerBuilderBase, CONFIG extends MongockConfiguration>
    implements RunnerBuilder<BUILDER_TYPE, CONFIG>, Validable {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilderBase.class);
  private static final Function<Parameter, String> DEFAULT_PARAM_NAME_PROVIDER = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;


  protected List<String> changeLogsScanPackage = new ArrayList<>();
  protected List<Class<?>> changeLogsScanClasses = new ArrayList<>();
  protected boolean trackIgnored = false;
  protected boolean throwExceptionIfCannotObtainLock = true;
  protected boolean enabled = true;
  protected String startSystemVersion = "0";
  protected String endSystemVersion = String.valueOf(Integer.MAX_VALUE);
  protected String serviceIdentifier = null;
  protected Map<String, Object> metadata;
  protected ConnectionDriver driver;
  protected AnnotationProcessor annotationProcessor;
  protected LegacyMigration legacyMigration = null;
  protected Collection<ChangeSetDependency> dependencies = new ArrayList<>();
  protected Function<Class, Object> changeLogInstantiator;


  protected RunnerBuilderBase() {
  }

  @Override
  public BUILDER_TYPE setDriver(ConnectionDriver driver) {
    this.driver = driver;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE addChangeLogsScanPackages(List<String> changeLogsScanPackageList) {
    if (changeLogsScanPackageList != null) {
      changeLogsScanPackage.addAll(changeLogsScanPackageList);
    }
    return getInstance();
  }

  @Override
  public BUILDER_TYPE addChangeLogClasses(List<Class<?>> classes) {
    if (classes != null) {
      changeLogsScanClasses.addAll(classes);
    }
    return getInstance();
  }


  @Override
  public BUILDER_TYPE setLegacyMigration(LegacyMigration legacyMigration) {
    this.legacyMigration = legacyMigration;
    if (legacyMigration != null) {
      changeLogsScanPackage.add(driver.getLegacyMigrationChangeLogClass(legacyMigration.isRunAlways()).getPackage().getName());
    }
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setChangeLogInstantiator(Function<Class, Object> changeLogInstantiator) {
    this.changeLogInstantiator = changeLogInstantiator;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setEnabled(boolean enabled) {
    this.enabled = enabled;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setTrackIgnored(boolean trackIgnored) {
    this.trackIgnored = trackIgnored;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE dontFailIfCannotAcquireLock() {
    this.throwExceptionIfCannotObtainLock = false;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setServiceIdentifier(String serviceIdentifier) {
    this.serviceIdentifier = serviceIdentifier;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE withMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE addDependency(String name, Class type, Object instance) {
    dependencies.add(new ChangeSetDependency(name, type, instance));
    return getInstance();
  }

  @Override
  public BUILDER_TYPE  setConfig(CONFIG config) {
    this.addScanItemsFromConfig(config.getChangeLogsScanPackage());
    if (!config.isThrowExceptionIfCannotObtainLock()) {
      this.dontFailIfCannotAcquireLock();
    }
    this
        .setTrackIgnored(config.isTrackIgnored())
        .setEnabled(config.isEnabled())
        .setStartSystemVersion(config.getStartSystemVersion())
        .setEndSystemVersion(config.getEndSystemVersion())
        .setServiceIdentifier(config.getServiceIdentifier())
        .withMetadata(config.getMetadata())
        .setLegacyMigration(config.getLegacyMigration());
    return getInstance();
  }

  ///////////////////////////////////////////////////
  // PRIVATE METHODS
  ///////////////////////////////////////////////////

  private void addScanItemsFromConfig(List<String> changeLogsScanPackage) {
    for (String itemPath : changeLogsScanPackage) {
      try {
        addChangeLogClass(ClassLoader.getSystemClassLoader().loadClass(itemPath));
      } catch (ClassNotFoundException e) {
        addChangeLogsScanPackage(itemPath);
      }
    }
  }

  @Deprecated
  public BUILDER_TYPE overrideAnnoatationProcessor(AnnotationProcessor annotationProcessor) {
    this.annotationProcessor = annotationProcessor;
    return getInstance();
  }

  protected MigrationExecutor buildMigrationExecutor() {
    return buildMigrationExecutor(DEFAULT_PARAM_NAME_PROVIDER);
  }

  protected MigrationExecutor buildMigrationExecutor(Function<Parameter, String> paramNameExtractor) {
    return new MigrationExecutorImpl(
        driver,
        buildDependencyManager(),
        new MigrationExecutorConfiguration(trackIgnored, serviceIdentifier),
        metadata,
        paramNameExtractor
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

  protected ChangeLogService getChangeLogService() {
    return new ChangeLogService(
        changeLogsScanPackage,
        changeLogsScanClasses,
        startSystemVersion,
        endSystemVersion,
        getAnnotationFilter(),
        annotationProcessor, // if null, it will take default MongockAnnotationManager
        changeLogInstantiator
    );
  }

  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotatedElement -> true;
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

  protected abstract BUILDER_TYPE getInstance();


}
