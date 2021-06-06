package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.standalone.migration.MigrationStandaloneBuilderBase;

public interface MigrationStandaloneBuilder extends MigrationStandaloneBuilderBase<MigrationStandaloneBuilder, ChangeEntry, MongockConfiguration> {
}
