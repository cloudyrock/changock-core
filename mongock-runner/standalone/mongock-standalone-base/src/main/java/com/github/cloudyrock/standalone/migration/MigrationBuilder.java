package com.github.cloudyrock.standalone.migration;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.migration.MigrationBuilderBase;

public interface MigrationBuilder extends MigrationBuilderBase<MigrationBuilder, Boolean, MongockConfiguration> {
}
