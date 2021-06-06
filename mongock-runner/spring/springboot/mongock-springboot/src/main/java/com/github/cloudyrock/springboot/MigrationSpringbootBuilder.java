package com.github.cloudyrock.springboot;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.springboot.base.builder.migration.MigrationSpringbootBuilderBase;

//TODO javadoc
public interface MigrationSpringbootBuilder extends MigrationSpringbootBuilderBase<MigrationSpringbootBuilder, ChangeEntry, MongockConfiguration> {
}
