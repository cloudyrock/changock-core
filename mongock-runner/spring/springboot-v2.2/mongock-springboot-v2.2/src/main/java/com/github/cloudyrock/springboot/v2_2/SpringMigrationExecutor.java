package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManagerWithContext;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import com.github.cloudyrock.spring.util.SpringMigrationExecutorBase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;

public class SpringMigrationExecutor<CHANGE_ENTRY extends ChangeEntry> extends SpringMigrationExecutorBase<CHANGE_ENTRY> {
  public SpringMigrationExecutor(ConnectionDriver driver, DependencyManagerWithContext dependencyManager, MigrationExecutorConfiguration config, Map<String, Object> metadata) {
    super(driver, dependencyManager, config, metadata);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void executeAndLogChangeSet(String executionId, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    super.executeAndLogChangeSet(executionId, changelogInstance, changeSetItem);
  }

  @Override
  protected String getParameterName(Parameter parameter) {
    String name = parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
    if(name == null) {
      name = parameter.isAnnotationPresent(Qualifier.class) ? parameter.getAnnotation(Qualifier.class).value() : null;
    }
    return name;
  }
}
