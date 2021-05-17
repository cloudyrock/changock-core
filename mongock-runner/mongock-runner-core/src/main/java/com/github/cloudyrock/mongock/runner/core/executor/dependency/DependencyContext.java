package com.github.cloudyrock.mongock.runner.core.executor.dependency;

import java.util.Optional;

public interface DependencyContext {

  <T> Optional<T> getBean(Class<T> type);

  Optional<Object> getBean(String name);
}
