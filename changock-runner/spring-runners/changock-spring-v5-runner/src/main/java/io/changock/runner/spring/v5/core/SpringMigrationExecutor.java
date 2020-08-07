package io.changock.runner.spring.v5.core;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.migration.api.ChangeLogItem;
import io.changock.migration.api.ChangeSetItem;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.MigrationExecutorConfiguration;
import io.changock.runner.core.DependencyManagerWithContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Child class from MigrationExecutor to force SpringDependencyManager
 */
public class SpringMigrationExecutor<CHANGE_ENTRY extends ChangeEntry> extends MigrationExecutor<CHANGE_ENTRY> {
  public SpringMigrationExecutor(ConnectionDriver driver, DependencyManagerWithContext dependencyManager, MigrationExecutorConfiguration config, Map<String, Object> metadata) {
    super(driver, dependencyManager, config, metadata);
  }

  @Override
  public void initializationAndValidation() throws ChangockException {
    super.initializationAndValidation();
    ((DependencyManagerWithContext) this.dependencyManager).runValidation();
  }

  @Override
  public void executeMigration(SortedSet<ChangeLogItem> changeLogs) {
    super.executeMigration(changeLogs);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void executeAndLogChangeSet(String executionId, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    super.executeAndLogChangeSet(executionId, changelogInstance, changeSetItem);
  }


  /**
   * Return Parameter qualifier by checking javax.inject.Named and spring Qualifier annotations, in this order
   * @param parameter parameter to be checked.
   * @return name of the parameter, if qualified. Null otherwise
   */
  @Override
  protected String getParameterName(Parameter parameter) {
    String name = parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
    if(name == null) {
      name = parameter.isAnnotationPresent(Qualifier.class) ? parameter.getAnnotation(Qualifier.class).value() : null;
    }
    return name;
  }

}
