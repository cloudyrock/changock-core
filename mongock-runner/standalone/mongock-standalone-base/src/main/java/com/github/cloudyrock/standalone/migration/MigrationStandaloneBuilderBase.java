package com.github.cloudyrock.standalone.migration;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.builder.MigrationBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.MigrationFailureEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationStartedEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationSuccessEvent;

import java.util.function.Consumer;

public interface MigrationStandaloneBuilderBase<
    SELF extends MigrationStandaloneBuilderBase<SELF, CHANGE_ENTRY, CONFIG>,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends MongockConfiguration>
    extends MigrationBuilderBase<SELF, CHANGE_ENTRY, Boolean, CONFIG> {

  //TODO javadoc
  SELF setMigrationStartedListener(Consumer<MigrationStartedEvent> listener);

  //TODO javadoc
  SELF setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener);

  //TODO javadoc
  SELF setMigrationFailureListener(Consumer<MigrationFailureEvent> listener);
}
