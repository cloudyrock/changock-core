package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.TransactionStrategy;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.driver.DriverLegaciable;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerImpl;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.function.Function;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;


public abstract class RunnerBuilderBase<
    SELF extends RunnerBuilderBase<SELF, R, CHANGELOG, CHANGESET, CHANGE_ENTRY, CONFIG>,
    R,
    CHANGELOG extends ChangeLogItem<CHANGESET>,
    CHANGESET extends ChangeSetItem,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends MongockConfiguration> {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilderBase.class);
  protected final Operation<R> operation;
  protected final CONFIG config;//todo make it independent from external configuration
  protected final ExecutorFactory<CHANGELOG, ? extends ChangeSetItem, CHANGE_ENTRY, CONFIG, R> executorFactory;
  protected final ChangeLogServiceBase<CHANGELOG, CHANGESET> changeLogService;
  protected final DependencyManager dependencyManager;
  protected EventPublisher<R> eventPublisher = new EventPublisher<>();
  protected ConnectionDriver<CHANGE_ENTRY> driver;
  protected Function<Class<?>, Object> changeLogInstantiator;
  protected Function<Parameter, String> parameterNameFunction = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;

  //todo move to config
  private String executionId = String.format("%s-%d", LocalDateTime.now(), new Random().nextInt(999));


  protected RunnerBuilderBase(Operation<R> operation,
                              ExecutorFactory<CHANGELOG, ? extends ChangeSetItem, CHANGE_ENTRY, CONFIG, R> executorFactory,
                              ChangeLogServiceBase<CHANGELOG, CHANGESET> changeLogService,
                              DependencyManager dependencyManager,
                              CONFIG config) {
    this.operation = operation;
    this.executorFactory = executorFactory;
    this.changeLogService = changeLogService;
    this.dependencyManager = dependencyManager;
    this.config = config;
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  SETTERS
  ///////////////////////////////////////////////////////////////////////////////////

  public SELF setExecutionId(String executionId) {
    this.executionId = executionId;
    return getInstance();
  }

  public SELF setChangeLogInstantiator(Function<Class<?>, Object> changeLogInstantiator) {
    this.changeLogInstantiator = changeLogInstantiator;
    return getInstance();
  }

  public CONFIG getConfig() {
    return config;
  }

  public SELF setConfig(CONFIG newConfig) {
    config.updateFrom(newConfig);
    return getInstance();
  }

  public SELF setDriver(ConnectionDriver<CHANGE_ENTRY> driver) {
    this.driver = driver;
    return getInstance();
  }


  public DependencyManager getDependencyManager() {
    return dependencyManager;
  }
  
  public SELF setTransactionStrategy(TransactionStrategy transactionStrategy) {
    config.setTransactionStrategy(transactionStrategy);
    return getInstance();
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  Build methods
  ///////////////////////////////////////////////////////////////////////////////////

  public MongockRunner<R> buildRunner() {
    return buildRunner(driver);
  }

  protected MongockRunner<R> buildRunner(ConnectionDriver<CHANGE_ENTRY> driver) {
    validateConfigurationAndInjections(driver);
    try {
      beforeBuildRunner();
      return new MongockRunnerImpl<>(
          buildExecutor(driver),
          config.isThrowExceptionIfCannotObtainLock(),
          config.isEnabled(),
          eventPublisher);
    } catch (MongockException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new MongockException(ex);
    }
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


  private SortedSet<CHANGELOG> getChangeLogs() {
    List<Class<?>> changeLogsScanClasses = new ArrayList<>();
    List<String> changeLogsScanPackage = new ArrayList<>();
    for (String itemPath : config.getChangeLogsScanPackage()) {
      try {
        changeLogsScanClasses.add(ClassLoader.getSystemClassLoader().loadClass(itemPath));
      } catch (ClassNotFoundException e) {
        changeLogsScanPackage.add(itemPath);
      }
    }
    changeLogService.setChangeLogsBasePackageList(changeLogsScanPackage);
    changeLogService.setChangeLogsBaseClassList(changeLogsScanClasses);
    changeLogService.setStartSystemVersion(config.getStartSystemVersion());
    changeLogService.setEndSystemVersion(config.getEndSystemVersion());
    changeLogService.setProfileFilter(getAnnotationFilter());
    changeLogService.setChangeLogInstantiator(changeLogInstantiator);
    return changeLogService.fetchChangeLogs();
  }


  protected void validateConfigurationAndInjections(ConnectionDriver<CHANGE_ENTRY> driver) throws MongockException {
    if (driver == null) {
      throw new MongockException("Driver must be injected to Mongock builder");
    }
    if (config.getChangeLogsScanPackage() == null || config.getChangeLogsScanPackage().isEmpty()) {
      throw new MongockException("Scan package for changeLogs is not set: use appropriate setter");
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

    if(config.getTransactionEnabled().isPresent()) {
      boolean transactionEnabled = config.getTransactionEnabled().get();
      if(transactionEnabled && !driver.isTransactionable()) {
        throw new MongockException("Property transaction-enabled=true, but transactionManager not provided");
      }

      if(!transactionEnabled && driver.isTransactionable()) {
        logger.warn("Property transaction-enabled=false, but driver is transactionable");
      }
    } else {
      logger.warn("Property transaction-enabled not provided. It will become true as default in next versions. Set explicit value to false in case transaction are not desired.");
      
      if(driver.isTransactionable()) {
        logger.warn("Property transaction-enabled not provided, but driver is transactionable. BY DEFAULT MONGOCK WILL RUN IN TRANSACTION MODE.");
      }
      else {
        logger.warn("Property transaction-enabled not provided and is unknown if driver is transactionable. BY DEFAULT MONGOCK WILL RUN IN NO-TRANSACTION MODE.");
      }
    }

  }


  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotatedElement -> true;
  }

  protected DriverLegaciable getDriverLegaciable() {
    return this.driver;
  }

  protected Executor<R> buildExecutor(ConnectionDriver<CHANGE_ENTRY> driver) {
    return executorFactory.getExecutor(
        operation,
        executionId,
        getChangeLogs(),
        driver,
        dependencyManager,
        parameterNameFunction,
        config
    );
  }

  public abstract SELF getInstance();

}
