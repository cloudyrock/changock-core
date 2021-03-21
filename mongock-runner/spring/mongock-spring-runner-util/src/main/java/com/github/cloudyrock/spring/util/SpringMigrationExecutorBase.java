package com.github.cloudyrock.spring.util;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManagerWithContext;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;

import java.util.Map;
import java.util.SortedSet;

/**
 * Child class from MigrationExecutor to force SpringDependencyManager
 */
public abstract class SpringMigrationExecutorBase<CHANGE_ENTRY extends ChangeEntry> extends MigrationExecutor<CHANGE_ENTRY> {
  public SpringMigrationExecutorBase(ConnectionDriver driver, DependencyManagerWithContext dependencyManager, MigrationExecutorConfiguration config, Map<String, Object> metadata) {
    super(driver, dependencyManager, config, metadata);
  }

  @Override
  public void initializationAndValidation() throws MongockException {
    super.initializationAndValidation();
    ((DependencyManagerWithContext) this.dependencyManager).runValidation();
  }

  @Override
  public void executeMigration(SortedSet<ChangeLogItem> changeLogs) {
    super.executeMigration(changeLogs);
  }



}
