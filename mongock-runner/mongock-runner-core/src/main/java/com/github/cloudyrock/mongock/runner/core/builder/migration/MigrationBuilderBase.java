package com.github.cloudyrock.mongock.runner.core.builder.migration;

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
public interface MigrationBuilderBase<BUILDER_TYPE extends MigrationBuilderBase, RETURN_TYPE, CONFIG extends MongockConfiguration>
    extends
    ChangeLogScanner<BUILDER_TYPE>,
    ChangeLogWriter<BUILDER_TYPE>,
    LegacyMigrator<BUILDER_TYPE>,
    DriverConnectable<BUILDER_TYPE>,
    Configurable<BUILDER_TYPE, CONFIG>,
    SystemVersionable<BUILDER_TYPE>,
    DependencyInjectable<BUILDER_TYPE>,
    ServiceIdentificable<BUILDER_TYPE>,
    RunnerBuilder<BUILDER_TYPE, RETURN_TYPE>,
    SelfInstanstiator<BUILDER_TYPE> {
}
