package com.github.cloudyrock.mongock.runner.core.executor.changelog;

@FunctionalInterface
public interface ChangeLogInstantiator {
  <T> T instantiate(Class<T> c) throws Exception;
}
