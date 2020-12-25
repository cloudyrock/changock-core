package io.changock.runner.core.executor;

import io.changock.driver.api.lock.LockCheckException;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.event.MigrationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangockBase<EXECUTOR extends MigrationExecutor> {
  private static final Logger logger = LoggerFactory.getLogger(ChangockBase.class);

  private final boolean enabled;
  private final EXECUTOR executor;
  private final ChangeLogService chanLogService;
  private final boolean throwExceptionIfCannotObtainLock;
  private final EventPublisher eventPublisher;

  protected ChangockBase(EXECUTOR executor,
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
   * @return true if Changock runner is enabled and able to run, otherwise false
   */
  public boolean isEnabled() {
    return enabled;
  }

  public void execute() throws ChangockException {
    if (!isEnabled()) {
      logger.info("Changock is disabled. Exiting.");
    } else {
      try {
        this.validate();
        eventPublisher.publishMigrationStarted();
        //todo create the migration result
        executor.executeMigration(chanLogService.fetchChangeLogs());
        eventPublisher.publishMigrationSuccessEvent(new MigrationResult());

      } catch (LockCheckException lockEx) {
        ChangockException changockException = new ChangockException(lockEx);
        eventPublisher.publishMigrationFailedEvent(changockException);
        if (throwExceptionIfCannotObtainLock) {
          logger.error("Changock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION", lockEx);
          throw changockException;

        } else {
          logger.warn("Changock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION", lockEx);
        }

      } catch (Exception ex) {
        ChangockException exWrapper = ChangockException.class.isAssignableFrom(ex.getClass()) ? (ChangockException) ex : new ChangockException(ex);
        logger.error("Error in changock process. ABORTED MIGRATION", exWrapper);
        eventPublisher.publishMigrationFailedEvent(exWrapper);
        throw exWrapper;

      }
    }
  }

  protected void validate() throws ChangockException {
    chanLogService.runValidation();
  }
}
