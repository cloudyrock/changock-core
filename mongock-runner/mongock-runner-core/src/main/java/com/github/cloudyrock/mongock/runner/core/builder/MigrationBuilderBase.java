package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.builder.roles.ChangeLogScanner;
import com.github.cloudyrock.mongock.runner.core.builder.roles.ChangeLogWriter;
import com.github.cloudyrock.mongock.runner.core.builder.roles.Configurable;
import com.github.cloudyrock.mongock.runner.core.builder.roles.DependencyInjectable;
import com.github.cloudyrock.mongock.runner.core.builder.roles.DriverConnectable;
import com.github.cloudyrock.mongock.runner.core.builder.roles.LegacyMigrator;
import com.github.cloudyrock.mongock.runner.core.builder.roles.RunnerBuilder;
import com.github.cloudyrock.mongock.runner.core.builder.roles.SelfInstanstiator;
import com.github.cloudyrock.mongock.runner.core.builder.roles.ServiceIdentificable;
import com.github.cloudyrock.mongock.runner.core.builder.roles.SystemVersionable;

@SuppressWarnings("all")
public interface MigrationBuilderBase<
    SELF extends MigrationBuilderBase<SELF, CHANGE_ENTRY, R, CONFIG>,
    CHANGE_ENTRY extends ChangeEntry,
    R,
    CONFIG extends MongockConfiguration>
    extends
    ChangeLogScanner<SELF, CONFIG>,
    ChangeLogWriter<SELF, CONFIG>,
    LegacyMigrator<SELF, CONFIG>,
    DriverConnectable<SELF, CHANGE_ENTRY, CONFIG>,
    Configurable<SELF, CONFIG>,
    SystemVersionable<SELF, CONFIG>,
    DependencyInjectable<SELF>,
    ServiceIdentificable<SELF, CONFIG>,
    RunnerBuilder<SELF, R, CONFIG>,
    SelfInstanstiator<SELF> {
}
