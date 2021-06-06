package com.github.cloudyrock.mongock.runner.core.executor.dependency;

import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.exception.MongockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * DependencyManager with support for ApplicationContext from Spring
 */
public class DependencyManagerWithContext extends DependencyManager implements Validable {

  private static final Logger logger = LoggerFactory.getLogger(DependencyManagerWithContext.class);

  private DependencyContext context;

  public void setContext(DependencyContext context) {
    this.context = context;
  }

  public boolean isContextPresent() {
    return context != null;
  }

  public DependencyContext getDependencyContext() {
    return context;
  }

  @Override
  public Optional<Object> getDependency(Class type, boolean lockGuarded) {
    return getDependency(type, null, lockGuarded);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Object> getDependency(Class type, String name, boolean lockGuarded) {
    Optional<Object> dependencyFromParent = super.getDependency(type, name, lockGuarded);
    if (dependencyFromParent.isPresent()) {
      return dependencyFromParent;
    } else if (context != null) {
      boolean byName = name != null && !name.isEmpty() && !ChangeSetDependency.DEFAULT_NAME.equals(name);
      Optional<Object> dependencyFromContext = byName ? context.getBean(name) : context.getBean(type);
      if (dependencyFromContext.isPresent()) {
        if (lockGuarded) {
          if (!type.isInterface()) {
            throw new MongockException(String.format("Parameter of type [%s] must be an interface or be annotated with @%s", type.getSimpleName(), NonLockGuarded.class.getSimpleName()));
          }
          return dependencyFromContext.map(instance -> lockGuardProxyFactory.getRawProxy(instance, type));
        } else {
          return dependencyFromContext;
        }
      } else {
        logger.warn("Dependency not found: {}", byName ? name : type.getSimpleName());
        return Optional.empty();
      }
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void runValidation() throws MongockException {
    if (context == null) {
      throw new MongockException("SpringContext not injected to SpringDependencyManager");
    }
  }
}
