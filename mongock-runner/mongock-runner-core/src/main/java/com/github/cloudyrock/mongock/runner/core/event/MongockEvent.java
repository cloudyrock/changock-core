package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;

public interface MongockEvent {

  MigrationResult getMigrationResult();
}
