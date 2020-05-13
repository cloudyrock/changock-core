package io.changock.runner.core;

import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.common.ForbiddenParameterException;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

@NotThreadSafe
public class DependencyManager {

  private final LinkedHashSet<ChangeSetDependency> connectorDependencies;
  private final LinkedHashSet<ChangeSetDependency> standardDependencies;
  private final ForbiddenParametersMap forbiddenParametersMap;
  private LockGuardProxyFactory lockGuardProxyFactory;

  public DependencyManager() {
    standardDependencies = new LinkedHashSet<>();
    connectorDependencies = new LinkedHashSet<>();
    forbiddenParametersMap = new ForbiddenParametersMap();
  }

  public Optional<Object> getDependency(Class type) throws ForbiddenParameterException {
    Optional<Object> dependencyOpt = forbiddenParametersMap.throwExceptionIfPresent(type)
        .or(() -> getDriverDependency(type));
    return dependencyOpt.isPresent() ? dependencyOpt : getStandardDependency(type);
  }

  private Optional<Object> getDriverDependency(Class type) {
    return getDependency(connectorDependencies, type);
  }

  private Optional<Object> getStandardDependency(Class type) {
    return getDependency(standardDependencies, type)
        .map(instance-> lockGuardProxyFactory.getRawProxy(instance, type));
  }

  private Optional<Object> getDependency(Collection<ChangeSetDependency> dependencyStore, Class type) {
    return dependencyStore
        .stream()
        .filter(dependency -> type.isAssignableFrom(dependency.getType()))
        .map(ChangeSetDependency::getInstance)
        .findFirst();
  }

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

  public DependencyManager addStandardDependency(Collection<? extends ChangeSetDependency> dependencies) {
    dependencies.forEach(this::addStandardDependency);
    return this;
  }

  public DependencyManager addStandardDependency(ChangeSetDependency dependency) {
    return addDependency(standardDependencies, dependency);
  }

  public DependencyManager addForbiddenParameters(ForbiddenParametersMap forbiddenParametersMap) {
    this.forbiddenParametersMap.putAll(forbiddenParametersMap);
    return this;
  }

  private <T extends ChangeSetDependency> DependencyManager addDependency(Collection<T> dependencyStore, T dependency) {
    if (!dependencyStore.add(dependency)) {
      dependencyStore.remove(dependency);
      dependencyStore.add(dependency);
    }
    return this;
  }


}
