package io.changock.runner.core.executor;

import com.github.cloudyrock.mongock.exception.MongockException;
import io.changock.driver.api.common.Validable;
import io.changock.driver.api.driver.ChangeSetDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * DependencyManager with support for ApplicationContext from Spring
 */
public class DependencyManagerWithContext extends DependencyManager implements Validable {

  private static final Logger logger = LoggerFactory.getLogger(DependencyManagerWithContext.class);

  private final DependencyContext context;

  public DependencyManagerWithContext(DependencyContext context) {
    this.context = context;
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
      try {
        boolean byName = name != null && !name.isEmpty() && !ChangeSetDependency.DEFAULT_NAME.equals(name);
        Optional<Object> dependencyFromContext = Optional.of(
            byName ? context.getBean(name) : context.getBean(type));
        if (lockGuarded) {
          if (!type.isInterface()) {
            throw new MongockException(String.format("Parameter of type [%s] must be an interface", type.getSimpleName()));
          }
          return dependencyFromContext.map(instance -> lockGuardProxyFactory.getRawProxy(instance, type));
        } else {
          return dependencyFromContext;
        }
      } catch (DependencyNotFound ex) {
        logger.warn("Dependency not found: {}", ex.getMessage());
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
