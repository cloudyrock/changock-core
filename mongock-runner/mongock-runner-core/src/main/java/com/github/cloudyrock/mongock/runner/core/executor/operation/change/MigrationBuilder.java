package com.github.cloudyrock.mongock.runner.core.executor.operation.change;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.ChangeLogScanner;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.ChangeLogWriter;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.Configurable;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.DependencyInjectable;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.DriverConnectable;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.LegacyMigrator;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.RunnerBuilder;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.SelfInstanstiator;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.ServiceIdentificable;
import com.github.cloudyrock.mongock.runner.core.builder.interfaces.SystemVersionable;

@SuppressWarnings("all")
public interface MigrationBuilder<BUILDER_TYPE extends MigrationBuilder, RETUR_TYPE, CONFIG extends MongockConfiguration>
    extends
    ChangeLogScanner<BUILDER_TYPE>,
    ChangeLogWriter<BUILDER_TYPE>,
    LegacyMigrator<BUILDER_TYPE>,
    DriverConnectable<BUILDER_TYPE>,
    Configurable<BUILDER_TYPE, CONFIG>,
    SystemVersionable<BUILDER_TYPE>,
    DependencyInjectable<BUILDER_TYPE>,
    ServiceIdentificable<BUILDER_TYPE>,
    RunnerBuilder<BUILDER_TYPE, RETUR_TYPE>,
    SelfInstanstiator<BUILDER_TYPE> {


}
