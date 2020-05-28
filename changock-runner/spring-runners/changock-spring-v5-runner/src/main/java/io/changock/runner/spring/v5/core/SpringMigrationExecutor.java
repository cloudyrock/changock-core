package io.changock.runner.spring.v5.core;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.migration.api.ChangeLogItem;
import io.changock.migration.api.ChangeSetItem;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.spring.util.SpringDependencyManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Child class from MigrationExecutor to force SpringDependencyManager
 */
public class SpringMigrationExecutor<CHANGE_ENTRY extends ChangeEntry> extends MigrationExecutor<CHANGE_ENTRY> {
  public SpringMigrationExecutor(ConnectionDriver driver, SpringDependencyManager dependencyManager, long lockAcquiredForMinutes, int maxTries, long maxWaitingForLockMinutes, Map<String, Object> metadata) {
    super(driver, dependencyManager, lockAcquiredForMinutes, maxTries, maxWaitingForLockMinutes, metadata);
  }

  @Override
  public void initializationAndValidation() throws ChangockException {
    super.initializationAndValidation();
    ((SpringDependencyManager) this.dependencyManager).runValidation();
  }

  @Override
  public void executeMigration(List<ChangeLogItem> changeLogs) {
    super.executeMigration(changeLogs);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void executeAndLogChangeSet(String executionId, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    super.executeAndLogChangeSet(executionId, changelogInstance, changeSetItem);
  }

}
