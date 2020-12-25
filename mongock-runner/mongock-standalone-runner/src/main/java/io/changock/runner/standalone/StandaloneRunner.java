package io.changock.runner.standalone;

import io.changock.runner.core.executor.ChangeLogService;
import io.changock.runner.core.executor.ChangockBase;
import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.executor.MigrationExecutor;

public class StandaloneRunner extends ChangockBase {

  protected StandaloneRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }
}
