package io.changock.runner.core;

import io.changock.migration.api.exception.ChangockException;
import io.changock.driver.api.lock.LockCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangockBase {
  private static final Logger logger = LoggerFactory.getLogger(ChangockBase.class);

  private final boolean enabled;
  private final MigrationExecutor executor;
  private final ChangeLogService chanLogService;
  private final boolean throwExceptionIfCannotObtainLock;

  protected ChangockBase(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled) {
    this.executor = executor;
    this.chanLogService = changeLogService;
    this.enabled = enabled;
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;

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

  public final void execute() throws ChangockException {
    if (!isEnabled()) {
      logger.info("Changock is disabled. Exiting.");
    } else {
      try {
        this.validate();
        executor.executeMigration(chanLogService.fetchChangeLogs());

      } catch (LockCheckException lockEx) {
        if (throwExceptionIfCannotObtainLock) {
          logger.error("Changock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION. Exception PROPAGATED", lockEx);
          throw new ChangockException(lockEx);

        } else {
          logger.warn("Changock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION. Exception NOT propagated", lockEx);
        }

      } catch (Exception ex) {
        ChangockException exWrapper = ChangockException.class.isAssignableFrom(ex.getClass()) ? (ChangockException) ex : new ChangockException(ex);
        logger.error("Error in changock process. ABORTED MIGRATION. Exception PROPAGATED", exWrapper);
        throw exWrapper;

      }
    }
  }

  protected void validate() throws ChangockException {
    chanLogService.runValidation();
  }
}
