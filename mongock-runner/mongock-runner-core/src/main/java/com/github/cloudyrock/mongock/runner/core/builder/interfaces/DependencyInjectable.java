package com.github.cloudyrock.mongock.runner.core.builder.interfaces;

public interface DependencyInjectable<BUILDER_TYPE extends DependencyInjectable> {
  /**
   * Manually adds a dependency to be used in changeLogs, which can be retrieved by its own type
   *
   * @param instance dependency
   * @return builder for fluent interface
   */
  BUILDER_TYPE addDependency(Object instance);

  /**
   * Manually adds a dependency to be used in changeLogs, which can be retrieved by a name
   *
   * @param name     name for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  BUILDER_TYPE addDependency(String name, Object instance);

  /**
   * Manually adds a dependency to be used in changeLogs, which can be retrieved by a type
   *
   * @param type     type for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  BUILDER_TYPE addDependency(Class type, Object instance);

  /**
   * Manually adds a dependency to be used in changeLogs, which can be retrieved by a type or name
   *
   * @param name     name for which it should be searched by
   * @param type     type for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  BUILDER_TYPE addDependency(String name, Class type, Object instance);
}
