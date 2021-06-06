package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.ChangeLogItemBase;
import com.github.cloudyrock.mongock.config.executor.ExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;

import java.lang.reflect.Parameter;
import java.util.SortedSet;
import java.util.function.Function;

public interface ExecutorFactory<
    CHANGELOG extends ChangeLogItemBase,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends ExecutorConfiguration, R> {

  Executor<R> getExecutor(Operation<R> op,
                          SortedSet<CHANGELOG> changeLogs,
                          ConnectionDriver<CHANGE_ENTRY> driver,
                          DependencyManager dependencyManager,
                          Function<Parameter, String> parameterNameProvider,
                          CONFIG config);


}


