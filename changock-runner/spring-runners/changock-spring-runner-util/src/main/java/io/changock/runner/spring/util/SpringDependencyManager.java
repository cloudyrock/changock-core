package io.changock.runner.spring.util;

import io.changock.driver.api.common.Validable;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.DependencyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

/**
 * DependencyManager with support for ApplicationContext from Spring
 */
public class SpringDependencyManager extends DependencyManager implements Validable {

  private static final Logger logger = LoggerFactory.getLogger(SpringDependencyManager.class);

  private final ApplicationContext springContext;

  public SpringDependencyManager(ApplicationContext springContext) {
    this.springContext = springContext;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Object> getDependency(Class type) {
    Optional<Object> dependencyOpt = super.getDependency(type);
    if (dependencyOpt.isPresent()) {
      return dependencyOpt;
    } else if (springContext != null) {
      try {
        return Optional.of(springContext.getBean(type))
            .map(instance-> lockGuardProxyFactory.getRawProxy(instance, type));
      } catch (BeansException ex) {
        logger.warn("Dependency not found: {}", ex.getMessage());
        return Optional.empty();
      }
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void runValidation() throws ChangockException {
    if (springContext == null) {
      throw new ChangockException("SpringContext not injected to SpringDependencyManager");
    }
  }
}
