package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;

public class StandaloneRunner extends MongockRunnerBase {

  protected StandaloneRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }
}
