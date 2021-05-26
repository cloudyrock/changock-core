package com.github.cloudyrock.standalone.migration;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.MigrationBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.MigrationFailureEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationStartedEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationSuccessEvent;

import java.util.function.Consumer;

public interface MigrationStandaloneBuilderBase<BUILDER_TYPE extends MigrationStandaloneBuilderBase<BUILDER_TYPE, CONFIG>, CONFIG extends MongockConfiguration>
    extends MigrationBuilderBase<BUILDER_TYPE, Boolean, CONFIG> {

  //TODO javadoc
  BUILDER_TYPE setMigrationStartedListener(Consumer<MigrationStartedEvent> listener);

  //TODO javadoc
  BUILDER_TYPE setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener);

  //TODO javadoc
  BUILDER_TYPE setMigrationFailureListener(Consumer<MigrationFailureEvent> listener);
}
