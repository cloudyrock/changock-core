package com.github.cloudyrock.mongock.runner.core.executor;

import java.util.function.Function;

public class DefaultDependencyContext implements DependencyContext {


  private final Function<Class, Object> byType;
  private final Function<String, Object> byName;

  public DefaultDependencyContext(Function<Class, Object> byType, Function<String, Object> byName) {
    this.byType = byType;
    this.byName = byName;
  }

  @Override
  public <T> T getBean(Class<T> type) {
    return (T)byType.apply(type);
  }

  @Override
  public Object getBean(String name) {
    return byName.apply(name);
  }
}
