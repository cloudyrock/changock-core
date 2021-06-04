package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.driver.api.lock.LockCheckException;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MongockRunnerImpl<T> implements MongockRunner<T> {
  private static final Logger logger = LoggerFactory.getLogger(MongockRunnerImpl.class);

  private final boolean enabled;
  private final Executor<T> executor;
  private final ChangeLogService chanLogService;
  private final boolean throwExceptionIfCannotObtainLock;
  private final EventPublisher eventPublisher;


  public MongockRunnerImpl(Executor<T> executor,
                           ChangeLogService changeLogService,
                           boolean throwExceptionIfCannotObtainLock,
                           boolean enabled,
                           EventPublisher eventPublisher) {
    this.executor = executor;
    this.chanLogService = changeLogService;
    this.enabled = enabled;
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    this.eventPublisher = eventPublisher;
  }

  /**
   * @return true if an execution is in progress, in any process.
   */
  public boolean isExecutionInProgress() {
    return executor.isExecutionInProgress();
  }

  /**
   * @return true if Mongock runner is enabled and able to run, otherwise false
   */
  public boolean isEnabled() {
    return enabled;
  }

  public Optional<T> execute() throws MongockException {
    if (!isEnabled()) {
      logger.info("Mongock is disabled. Exiting.");
      return Optional.empty();
    } else {
      try {
        this.validate();
        eventPublisher.publishMigrationStarted();
        T result = executor.executeMigration(chanLogService.fetchChangeLogs());
        eventPublisher.publishMigrationSuccessEvent(MigrationResult.successResult());
        return Optional.ofNullable(result);

      } catch (LockCheckException lockEx) {
        MongockException mongockException = new MongockException(lockEx);
        eventPublisher.publishMigrationFailedEvent(mongockException);
        if (throwExceptionIfCannotObtainLock) {
          logger.error("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION", lockEx);
          throw mongockException;

        } else {
          logger.warn("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION", lockEx);
          return Optional.empty();
        }

      } catch (Exception ex) {
        MongockException exWrapper = MongockException.class.isAssignableFrom(ex.getClass()) ? (MongockException) ex : new MongockException(ex);
        logger.error("Error in mongock process. ABORTED MIGRATION", exWrapper);
        eventPublisher.publishMigrationFailedEvent(exWrapper);
        throw exWrapper;

      }
    }
  }

  protected void validate() throws MongockException {
    chanLogService.runValidation();
  }
}
