package com.github.cloudyrock.mongock.runner.core.executor;

@FunctionalInterface
public interface ChangeLogInstantiator {
  <T> T instantiate(Class<T> c) throws Exception;
}
