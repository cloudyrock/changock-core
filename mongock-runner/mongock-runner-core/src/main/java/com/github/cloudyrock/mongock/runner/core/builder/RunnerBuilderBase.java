package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
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

  protected CONFIG config;
  protected ConnectionDriver driver;
  protected AnnotationProcessor annotationProcessor;
  protected Collection<ChangeSetDependency> dependencies = new ArrayList<>();
  protected Function<Class, Object> changeLogInstantiator;
  protected ExecutorFactory executorFactory;

  protected RunnerBuilderBase(ExecutorFactory executorFactory, CONFIG config) {
    this.executorFactory = executorFactory;
    this.config = config;
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  Properties setters
  ///////////////////////////////////////////////////////////////////////////////////
  @Override
  public BUILDER_TYPE addChangeLogsScanPackages(List<String> changeLogsScanPackageList) {
    if (changeLogsScanPackageList != null) {
      config.getChangeLogsScanPackage().addAll(changeLogsScanPackageList);
    }
    return getInstance();
  }

  @Override
  public BUILDER_TYPE addChangeLogClasses(List<Class<?>> classes) {
    if (classes != null) {
      classes.stream().map(Class::getName).forEach(config.getChangeLogsScanPackage()::add);
    }
    return getInstance();
  }


  @Override
  public BUILDER_TYPE setLegacyMigration(LegacyMigration legacyMigration) {
    config.setLegacyMigration(legacyMigration);
    if (legacyMigration != null) {
      config.getChangeLogsScanPackage().add(driver.getLegacyMigrationChangeLogClass(legacyMigration.isRunAlways()).getPackage().getName());
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
    this.config.setEnabled(enabled);
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setTrackIgnored(boolean trackIgnored) {
    config.setTrackIgnored(trackIgnored);
    return getInstance();
  }

  @Override
  public BUILDER_TYPE dontFailIfCannotAcquireLock() {
    this.config.setThrowExceptionIfCannotObtainLock(false);
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setStartSystemVersion(String startSystemVersion) {
    config.setStartSystemVersion(startSystemVersion);
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setEndSystemVersion(String endSystemVersion) {
    config.setEndSystemVersion(endSystemVersion);
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setServiceIdentifier(String serviceIdentifier) {
    config.setServiceIdentifier(serviceIdentifier);
    return getInstance();
  }

  @Override
  public BUILDER_TYPE withMetadata(Map<String, Object> metadata) {
    config.setMetadata(metadata);
    return getInstance();
  }

  @Override
  public BUILDER_TYPE setConfig(CONFIG newConfig) {
    config.updateFrom(newConfig);
    return getInstance();
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  Injections setters
  ///////////////////////////////////////////////////////////////////////////////////
  @Override
  public BUILDER_TYPE setDriver(ConnectionDriver driver) {
    this.driver = driver;
    return getInstance();
  }

  @Override
  public BUILDER_TYPE addDependency(String name, Class type, Object instance) {
    dependencies.add(new ChangeSetDependency(name, type, instance));
    return getInstance();
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  Build methods
  ///////////////////////////////////////////////////////////////////////////////////

  protected <T> MongockRunner<T> buildRunner(Operation<T> operation) {
    runValidation();
    beforeBuildRunner();
    return new MongockRunner<>(
        buildExecutor(operation),
        buildChangeLogService(),
        config.isThrowExceptionIfCannotObtainLock(),
        config.isEnabled(),
        buildEventPublisher());
  }

  protected void beforeBuildRunner() {
  }


  protected final <T> Executor<T> buildExecutor(Operation<T> operation) {
    return executorFactory.getExecutor(
        operation,
        driver,
        buildDependencyManager(),
        buildParameterNameFunction(),
        config
    );
  }


  protected Function<Parameter, String> buildParameterNameFunction() {
    return parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
  }

  protected DependencyManager buildDependencyManager() {
    DependencyManager dependencyManager = new DependencyManager();
    if (config.getLegacyMigration() != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, config.getLegacyMigration())
      );
    }
    dependencyManager.addStandardDependencies(dependencies);
    return dependencyManager;
  }

  protected ChangeLogService buildChangeLogService() {

    List<Class<?>> changeLogsScanClasses = new ArrayList<>();
    List<String> changeLogsScanPackage = new ArrayList<>();
    for (String itemPath : config.getChangeLogsScanPackage()) {
      try {
        changeLogsScanClasses.add(ClassLoader.getSystemClassLoader().loadClass(itemPath));
      } catch (ClassNotFoundException e) {
        changeLogsScanPackage.add(itemPath);
      }
    }
    return new ChangeLogService(
        changeLogsScanPackage,
        changeLogsScanClasses,
        config.getStartSystemVersion(),
        config.getEndSystemVersion(),
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
    if (config.getChangeLogsScanPackage() == null || config.getChangeLogsScanPackage().isEmpty()) {
      throw new MongockException("changeLogsScanPackage must be injected to Mongock builder");
    }
    if (!config.isThrowExceptionIfCannotObtainLock()) {
      logger.warn("throwExceptionIfCannotObtainLock is disabled, which means Mongock will continue even if it's not able to acquire the lock");
    }
    if (!"0".equals(config.getStartSystemVersion()) || !String.valueOf(Integer.MAX_VALUE).equals(config.getEndSystemVersion())) {
      logger.info("Running Mongock with startSystemVersion[{}] and endSystemVersion[{}]", config.getStartSystemVersion(), config.getEndSystemVersion());
    }
    if (config.getMetadata() == null) {
      logger.info("Running Mongock with NO metadata");
    } else {
      logger.info("Running Mongock with metadata");
    }
  }


  protected abstract EventPublisher buildEventPublisher();

  protected abstract BUILDER_TYPE getInstance();


}
