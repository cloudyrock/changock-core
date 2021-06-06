package com.github.cloudyrock.mongock.runner.core.executor.dependency;

import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.driver.api.common.ForbiddenParameterException;
import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NotThreadSafe
public class DependencyManager  implements Validable {

  private final LinkedHashSet<ChangeSetDependency> connectorDependencies;
  private final LinkedHashSet<ChangeSetDependency> standardDependencies;
  protected LockGuardProxyFactory lockGuardProxyFactory;

  public DependencyManager() {
    standardDependencies = new LinkedHashSet<>();
    connectorDependencies = new LinkedHashSet<>();
  }

  public Optional<Object> getDependency(Class type, boolean lockGuarded) throws ForbiddenParameterException {
    return getDependency(type, null, lockGuarded);
  }

  public Optional<Object> getDependency(Class type, String name, boolean lockGuarded) throws ForbiddenParameterException {
    Optional<Object> dependencyOpt = getDependencyFromStore(connectorDependencies, type, name);
    return dependencyOpt.isPresent() ? dependencyOpt : getStandardDependency(type, name, lockGuarded);
  }

  private Optional<Object> getStandardDependency(Class type, String name, boolean lockGuarded) {
    Optional<Object> dependencyOpt = getDependencyFromStore(standardDependencies, type, name);
    if (dependencyOpt.isPresent() && lockGuarded) {
      if (!type.isInterface()) {
        throw new MongockException(String.format("Parameter of type [%s] must be an interface or be annotated with @%s", type.getSimpleName(), NonLockGuarded.class.getSimpleName()));
      }
      return dependencyOpt.map(instance -> lockGuardProxyFactory.getRawProxy(instance, type));
    } else {
      return dependencyOpt;
    }
  }

  @SuppressWarnings("unchecked")
  private Optional<Object> getDependencyFromStore(Collection<ChangeSetDependency> dependencyStore, Class<?> type, String name) {
    boolean byName = name != null && !name.isEmpty() && !ChangeSetDependency.DEFAULT_NAME.equals(name);
    Predicate<ChangeSetDependency> filter = byName
        ? dependency -> name.equals(dependency.getName())
        : dependency -> type.isAssignableFrom(dependency.getType());

    Stream<ChangeSetDependency> stream = dependencyStore.stream().filter(filter);
    if(byName) {
      return stream.map(ChangeSetDependency::getInstance).findFirst();
    } else {
      return stream.reduce((dependency1, dependency2) -> !dependency1.isDefaultNamed() && dependency2.isDefaultNamed() ? dependency2 : dependency1)
          .map(ChangeSetDependency::getInstance);
    }
  }

  // setters

  public DependencyManager setLockGuardProxyFactory(LockGuardProxyFactory lockGuardProxyFactory) {
    this.lockGuardProxyFactory = lockGuardProxyFactory;
    return this;
  }

  public DependencyManager addDriverDependencies(Collection<? extends ChangeSetDependency> dependencies) {
    dependencies.forEach(this::addDriverDependency);
    return this;
  }

  public DependencyManager addDriverDependency(ChangeSetDependency dependency) {
    return addDependency(connectorDependencies, dependency);
  }

  public DependencyManager addStandardDependencies(Collection<? extends ChangeSetDependency> dependencies) {
    dependencies.forEach(this::addStandardDependency);
    return this;
  }

  public DependencyManager addStandardDependency(ChangeSetDependency dependency) {
    return addDependency(standardDependencies, dependency);
  }

  private <T extends ChangeSetDependency> DependencyManager addDependency(Collection<T> dependencyStore, T dependency) {
    //add returns false if it's already there. In that case, it needs to be removed and then inserted
    if (!dependencyStore.add(dependency)) {
      dependencyStore.remove(dependency);
      dependencyStore.add(dependency);
    }
    return this;
  }


  @Override
  public void runValidation() throws MongockException {
    //Not required
  }
}
