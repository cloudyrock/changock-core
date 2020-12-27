package com.github.cloudyrock.mongock.runner.core.executor;

public interface DependencyContext {

  <T> T getBean(Class<T> type);

  Object getBean(String name);
}
