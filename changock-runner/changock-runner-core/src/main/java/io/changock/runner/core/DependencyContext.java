package io.changock.runner.core;

public interface DependencyContext {

  <T> T getBean(Class<T> type);

  Object getBean(String name);
}
