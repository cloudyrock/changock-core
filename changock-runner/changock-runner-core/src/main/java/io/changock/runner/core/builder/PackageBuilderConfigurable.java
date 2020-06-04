package io.changock.runner.core.builder;

import java.util.List;

public interface PackageBuilderConfigurable<BUILDER_TYPE extends RunnerBuilderConfigurable, CONFIG extends ChangockConfiguration> {
  /**
   * Add a changeLog package to be scanned.
   * <b>Mandatory</b>
   * @param changeLogsScanPackage  package to be scanned
   * @return builder for fluent interface
   */
  BUILDER_TYPE addChangeLogsScanPackage(String changeLogsScanPackage);

  /**
   * Adds a list of packages(or classes by its full classname) to be scanned  to the list.
   * Mongo allows multiple packages/classes
   * <b>Requires at least one package/class</b>
   *
   * @param changeLogsScanPackage package to be scanned
   * @return builder for fluent interface
   */
  BUILDER_TYPE addChangeLogsScanPackages(List<String> changeLogsScanPackage);

  /**
   * Adds a class to be scanned  to the list. Mongo allows multiple packages/classes
   * <b>Requires at least one package/class</b>
   *
   * @param clazz package to be scanned
   * @return builder for fluent interface
   */
  BUILDER_TYPE addChangeLogClass(Class clazz);

  BUILDER_TYPE setConfig(CONFIG config);
}
