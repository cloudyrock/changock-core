package com.github.cloudyrock.mongock.runner.core.executor.operation.change;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeLogItemBase;
import com.github.cloudyrock.mongock.config.executor.ChangeExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Parameter;
import java.util.SortedSet;
import java.util.function.Function;

@NotThreadSafe
public class MigrationExecutor extends MigrationExecutorBase<ChangeLogItem, ChangeExecutorConfiguration> {



  public MigrationExecutor(SortedSet<ChangeLogItem> changeLogs,
                           ConnectionDriver driver,
                           DependencyManager dependencyManager,
                           Function<Parameter, String> parameterNameProvider,
                           ChangeExecutorConfiguration config) {
    super(changeLogs, driver, dependencyManager, parameterNameProvider, config);
  }
}
