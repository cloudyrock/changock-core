package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerImpl;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;
import com.github.cloudyrock.mongock.driver.api.driver.DriverLegaciable;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;


public abstract class RunnerBuilderBase<SELF extends RunnerBuilderBase<SELF, R, CONFIG>, R, CONFIG extends MongockConfiguration>
    implements Validable {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilderBase.class);

  protected final DependencyManager dependencyManager;
  protected EventPublisher eventPublisher = EventPublisher.empty();
  protected final Operation<R> operation;
  protected final CONFIG config;
  protected final ExecutorFactory<CONFIG> executorFactory;
  protected ConnectionDriver driver;
  protected Function<Class<?>, Object> changeLogInstantiator;
  protected Function<Parameter, String> parameterNameFunction = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;


  protected RunnerBuilderBase(Operation<R> operation, ExecutorFactory<CONFIG> executorFactory, CONFIG config, DependencyManager dependencyManager) {
    this.executorFactory = executorFactory;
    this.config = config;
    this.operation = operation;
    this.dependencyManager = dependencyManager;
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  SETTERS
  ///////////////////////////////////////////////////////////////////////////////////

  public SELF setChangeLogInstantiator(Function<Class<?>, Object> changeLogInstantiator) {
    this.changeLogInstantiator = changeLogInstantiator;
    return getInstance();
  }


  public SELF setConfig(CONFIG newConfig) {
    config.updateFrom(newConfig);
    return getInstance();
  }

  public CONFIG getConfig() {
    return config;
  }

  
  public SELF setDriver(ConnectionDriver driver) {
    this.driver = driver;
    return getInstance();
  }


  public DependencyManager getDependencyManager() {
    return dependencyManager;
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  Build methods
  ///////////////////////////////////////////////////////////////////////////////////

  public MongockRunner<R> buildRunner() {
    runValidation();
    beforeBuildRunner();
    return new MongockRunnerImpl<>(
        buildExecutor(driver),
        buildChangeLogService(),
        config.isThrowExceptionIfCannotObtainLock(),
        config.isEnabled(),
        eventPublisher);
  }

  protected void beforeBuildRunner() {
    if (config.getLegacyMigration() != null) {
      DriverLegaciable legaciableDriver = this.getDriverLegaciable();
      if (legaciableDriver != null) {
        config.getChangeLogsScanPackage().add(legaciableDriver.getLegacyMigrationChangeLogClass(config.getLegacyMigration().isRunAlways()).getPackage().getName());
        dependencyManager.addStandardDependency(
            new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, config.getLegacyMigration())
        );
      }
    }
  }
  
  protected DriverLegaciable getDriverLegaciable() {
    return this.driver;
  }


  protected final Executor<R> buildExecutor(ConnectionDriver driver) {
    return executorFactory.getExecutor(
        operation,
        driver,
        dependencyManager,
        parameterNameFunction,
        config
    );
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
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBasePackageList(changeLogsScanPackage);
    changeLogService.setChangeLogsBaseClassList(changeLogsScanClasses);
    changeLogService.setStartSystemVersion(config.getStartSystemVersion());
    changeLogService.setEndSystemVersion(config.getEndSystemVersion());
    changeLogService.setProfileFilter(getAnnotationFilter());
    changeLogService.setChangeLogInstantiator(changeLogInstantiator);
    return changeLogService;
  }

  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotatedElement -> true;
  }

  
  public void runValidation() throws MongockException {
    this.validateDriver();
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
  
  protected void validateDriver() throws MongockException {
    if (driver == null) {
      throw new MongockException("Driver must be injected to Mongock builder");
    }
  }


  public abstract SELF getInstance();

}
