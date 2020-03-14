package io.changock.runner.core;

import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.utils.annotation.NotThreadSafe;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

//TODO move to runner core module
@NotThreadSafe
public class DependencyManager {

  private final LinkedHashSet<ChangeSetDependency> connectorDependencies;
  private final LinkedHashSet<ChangeSetDependency> standardDependencies;

  public DependencyManager() {
    standardDependencies = new LinkedHashSet<>();
    connectorDependencies = new LinkedHashSet<>();
  }

  public DependencyManager addConnectorDependency(Collection<? extends ChangeSetDependency> dependencies) {
    dependencies.forEach(this::addConnectorDependency);
    return this;
  }

  public DependencyManager addConnectorDependency(ChangeSetDependency dependency) {
    return addDependency(connectorDependencies, dependency);
  }

  public DependencyManager addStandardDependency(Collection<? extends ChangeSetDependency> dependencies) {
    dependencies.forEach(this::addStandardDependency);
    return this;
  }

  public DependencyManager addStandardDependency(ChangeSetDependency dependency) {
    return addDependency(standardDependencies, dependency);
  }

  private <T extends ChangeSetDependency> DependencyManager addDependency(Collection<T> dependencyStore, T dependency) {
    if(!dependencyStore.add(dependency)) {
      dependencyStore.remove(dependency);
      dependencyStore.add(dependency);
    }
    return this;
  }

  public Optional<Object> getDependency(Class type) {
    Optional<Object> dependencyOpt = getConnectorDependency(type);
    return dependencyOpt.isPresent() ? dependencyOpt : getStandardDependency(type);
  }

  private Optional<Object> getConnectorDependency(Class type) {
    return getDependency(connectorDependencies, type);
  }

  private Optional<Object> getStandardDependency(Class type) {
    return getDependency(standardDependencies, type);
  }

  private Optional<Object> getDependency(Collection<ChangeSetDependency> dependencyStore, Class type) {
    return dependencyStore
        .stream()
        .filter(dependency -> type.isAssignableFrom(dependency.getType()))
        .map(ChangeSetDependency::getInstance)
        .findFirst();
  }


}
