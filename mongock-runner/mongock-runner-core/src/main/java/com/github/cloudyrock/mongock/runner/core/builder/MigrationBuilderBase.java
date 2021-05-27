package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
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
public interface MigrationBuilderBase<SELF extends MigrationBuilderBase<SELF, R, CONFIG>, R, CONFIG extends MongockConfiguration>
    extends
    ChangeLogScanner<SELF>,
    ChangeLogWriter<SELF>,
    LegacyMigrator<SELF>,
    DriverConnectable<SELF>,
    Configurable<SELF, CONFIG>,
    SystemVersionable<SELF>,
    DependencyInjectable<SELF>,
    ServiceIdentificable<SELF>,
    RunnerBuilder<SELF, R>,
    SelfInstanstiator<SELF> {
}
