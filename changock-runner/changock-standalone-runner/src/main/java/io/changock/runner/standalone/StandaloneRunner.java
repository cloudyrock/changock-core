package io.changock.runner.standalone;

import io.changock.runner.core.ChangeLogService;
import io.changock.runner.core.ChangockBase;
import io.changock.runner.core.EventPublisher;
import io.changock.runner.core.MigrationExecutor;

public class StandaloneRunner extends ChangockBase {

  protected StandaloneRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }
}
