package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Function;

public class ExecutorFactory {

  public <T> MigrationExecutor<T> getExecutor(Operation<T> op,
                                              ConnectionDriver driver,
                                              DependencyManager dependencyManager,
                                              Map<String, Object> metadata,
                                              Function<Parameter, String> parameterNameProvider) {
    if(op instanceof MigrationOp) {
      //return MigrationExecutor
    }
    return new MigrationExecutor<T>() {
      @Override
      public T executeMigration(SortedSet<ChangeLogItem> changeLogs) {
        return null;
      }

      @Override
      public boolean isExecutionInProgress() {
        return false;
      }
    };
  }


}
