package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Parameter;
import java.util.SortedSet;
import java.util.function.Function;

@NotThreadSafe
public class TestMigrationExecutor extends MigrationExecutor {


  public TestMigrationExecutor(String executionId,
                               SortedSet<ChangeLogItem<ChangeSetItem>> changeLogs,
                               ConnectionDriver<ChangeEntry> driver,
                               DependencyManager dependencyManager,
                               Function<Parameter, String> paramNameExtractor,
                               MongockConfiguration config) {
    //todo remove null
    super(executionId, changeLogs, driver, dependencyManager, paramNameExtractor, config);
  }



}
