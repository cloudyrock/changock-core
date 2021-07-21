package com.github.cloudyrock.mongock.micronaut.base.context;

import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyContext;
import io.micronaut.context.ApplicationContext;

import java.util.Optional;

public class MicronautDependencyContext implements DependencyContext {

  private final ApplicationContext micronautContext;

  public MicronautDependencyContext(ApplicationContext micronautContext) {
    this.micronautContext = micronautContext;
  }

  public ApplicationContext getMicronautContext() {
    return micronautContext;
  }

  @Override
  public <T> Optional<T> getBean(Class<T> type) {
      return Optional.ofNullable(micronautContext.getBean(type));
  }

  @Override
  public Optional<Object> getBean(String name) {
    try {
      return Optional.ofNullable(micronautContext.getBean(Class.forName(name)));
    } catch (ClassNotFoundException e) {
      return Optional.empty();
    }
  }
}
