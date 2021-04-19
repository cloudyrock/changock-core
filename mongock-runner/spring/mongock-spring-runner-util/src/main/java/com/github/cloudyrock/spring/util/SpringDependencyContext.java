package com.github.cloudyrock.spring.util;

import com.github.cloudyrock.mongock.runner.core.executor.DependencyContext;
import java.util.Optional;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class SpringDependencyContext implements DependencyContext {

  private final ApplicationContext springContext;

  public SpringDependencyContext(ApplicationContext springContext) {
    this.springContext = springContext;
  }

  @Override
  public <T> Optional<T> getBean(Class<T> type) {
    try {
      return Optional.ofNullable(springContext.getBean(type));
    } catch (BeansException ex) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Object> getBean(String name) {
    try {
      return Optional.ofNullable(springContext.getBean(name));
    } catch (BeansException ex) {
      return Optional.empty();
    }
  }
}
