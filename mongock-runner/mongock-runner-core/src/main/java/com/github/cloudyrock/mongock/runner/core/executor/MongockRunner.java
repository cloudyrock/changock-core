package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.driver.api.lock.LockCheckException;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongockRunner<T> {
  private static final Logger logger = LoggerFactory.getLogger(MongockRunner.class);

  private final boolean enabled;
  private final Executor executor;
  private final ChangeLogService chanLogService;
  private final boolean throwExceptionIfCannotObtainLock;
  private final EventPublisher eventPublisher;

  public MongockRunner(Executor executor,
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

  public void execute() throws MongockException {
    if (!isEnabled()) {
      logger.info("Mongock is disabled. Exiting.");
      return;
    } else {
      try {
        this.validate();
        eventPublisher.publishMigrationStarted();
        executor.executeMigration(chanLogService.fetchChangeLogs());
        eventPublisher.publishMigrationSuccessEvent(new MigrationResult());

      } catch (LockCheckException lockEx) {
        MongockException mongockException = new MongockException(lockEx);
        eventPublisher.publishMigrationFailedEvent(mongockException);
        if (throwExceptionIfCannotObtainLock) {
          logger.error("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION", lockEx);
          throw mongockException;

        } else {
          logger.warn("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION", lockEx);
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
