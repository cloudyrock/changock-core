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
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;


public abstract class RunnerBuilderBase<SELF extends RunnerBuilderBase<SELF, R, CONFIG>, R, CONFIG extends MongockConfiguration>
    implements Validable {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilderBase.class);

  protected final DependencyManager dependencyManager;
  protected EventPublisher eventPublisher = EventPublisher.empty();
  protected final Operation<R> operation;
  protected final CONFIG config;
  protected final ExecutorFactory<CONFIG> executorFactory;
  protected ConnectionDriver driver;
  protected AnnotationProcessor annotationProcessor;
  protected Function<Class<?>, Object> changeLogInstantiator;
  protected Function<Parameter, String> parameterNameFunction = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;


  protected RunnerBuilderBase(Operation<R> operation, ExecutorFactory<CONFIG> executorFactory, CONFIG config, DependencyManager dependencyManager) {
    this.executorFactory = executorFactory;
    this.config = config;
    this.operation = operation;
    this.dependencyManager = dependencyManager;
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  Properties setters
  ///////////////////////////////////////////////////////////////////////////////////

  public SELF setChangeLogInstantiator(Function<Class<?>, Object> changeLogInstantiator) {
    this.changeLogInstantiator = changeLogInstantiator;
    return getInstance();
  }

  
  public SELF setLegacyMigration(LegacyMigration legacyMigration) {
    config.setLegacyMigration(legacyMigration);
    return getInstance();
  }



  
  public SELF setEnabled(boolean enabled) {
    this.config.setEnabled(enabled);
    return getInstance();
  }

  
  public SELF setTrackIgnored(boolean trackIgnored) {
    config.setTrackIgnored(trackIgnored);
    return getInstance();
  }

  
  public SELF dontFailIfCannotAcquireLock() {
    this.config.setThrowExceptionIfCannotObtainLock(false);
    return getInstance();
  }

  
  public SELF setStartSystemVersion(String startSystemVersion) {
    config.setStartSystemVersion(startSystemVersion);
    return getInstance();
  }

  
  public SELF setEndSystemVersion(String endSystemVersion) {
    config.setEndSystemVersion(endSystemVersion);
    return getInstance();
  }

  
  public SELF setServiceIdentifier(String serviceIdentifier) {
    config.setServiceIdentifier(serviceIdentifier);
    return getInstance();
  }

  
  public SELF withMetadata(Map<String, Object> metadata) {
    config.setMetadata(metadata);
    return getInstance();
  }

  
  public SELF setConfig(CONFIG newConfig) {
    config.updateFrom(newConfig);
    return getInstance();
  }

  public CONFIG getConfig() {
    return config;
  }

  
  public SELF addDependency(Object instance) {
    return addDependency(instance.getClass(), instance);
  }

  
  public SELF addDependency(String name, Object instance) {
    return addDependency(name, instance.getClass(), instance);
  }

  
  public SELF addDependency(Class<?> type, Object instance) {
    return addDependency(ChangeSetDependency.DEFAULT_NAME, type, instance);
  }

  public SELF addDependency(String name, Class<?> type, Object instance) {
    dependencyManager.addStandardDependency(new ChangeSetDependency(name, type, instance));
    return getInstance();
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  Injections setters
  ///////////////////////////////////////////////////////////////////////////////////

  
  public SELF setDriver(ConnectionDriver driver) {
    this.driver = driver;
    return getInstance();
  }

  

  ///////////////////////////////////////////////////////////////////////////////////
  //  Build methods
  ///////////////////////////////////////////////////////////////////////////////////

  public MongockRunner<R> buildRunner() {
    runValidation();
    beforeBuildRunner();
    return new MongockRunner<>(
        buildExecutor(),
        buildChangeLogService(),
        config.isThrowExceptionIfCannotObtainLock(),
        config.isEnabled(),
        eventPublisher);
  }

  protected void beforeBuildRunner() {
    if (config.getLegacyMigration() != null) {
      config.getChangeLogsScanPackage().add(driver.getLegacyMigrationChangeLogClass(config.getLegacyMigration().isRunAlways()).getPackage().getName());
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, config.getLegacyMigration())
      );
    }
  }


  protected final Executor<R> buildExecutor() {
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

  public abstract SELF getInstance();

}
