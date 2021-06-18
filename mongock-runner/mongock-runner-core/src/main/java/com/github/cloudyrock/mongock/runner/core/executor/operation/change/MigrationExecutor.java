package com.github.cloudyrock.mongock.runner.core.executor.operation.change;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.config.executor.ChangeExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeState;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Parameter;
import java.util.SortedSet;
import java.util.function.Function;

@NotThreadSafe
public class MigrationExecutor extends MigrationExecutorBase<ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, ChangeExecutorConfiguration> {


  public MigrationExecutor(String executionId,
                           SortedSet<ChangeLogItem<ChangeSetItem>> changeLogs,
                           ConnectionDriver<ChangeEntry> driver,
                           DependencyManager dependencyManager,
                           Function<Parameter, String> parameterNameProvider,
                           ChangeExecutorConfiguration config) {
    super(executionId, changeLogs, driver, dependencyManager, parameterNameProvider, config);
  }


  @Override
  protected ChangeEntry createChangeEntryInstance(String executionId, String executionHostname, ChangeSetItem changeSetItem, long executionTimeMillis, ChangeState state) {
    return ChangeEntry.createInstance(executionId, state, changeSetItem, executionTimeMillis, executionHostname, metadata);
  }

  @Override
  protected boolean isTransactionOfAnyKindEnabled() {
    return driver.getTransactioner().isPresent();
  }
}
