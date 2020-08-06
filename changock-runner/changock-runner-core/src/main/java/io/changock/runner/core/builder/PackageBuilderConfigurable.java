package io.changock.runner.core.builder;

import io.changock.runner.core.builder.configuration.ChangockConfiguration;

import java.util.Collections;
import java.util.List;

public interface PackageBuilderConfigurable<BUILDER_TYPE extends RunnerBuilderConfigurable, CONFIG extends ChangockConfiguration> {


  BUILDER_TYPE setConfig(CONFIG config);

  /**
   * Adds a list of packages(or classes by its full classname) to be scanned  to the list.
   * Mongo allows multiple packages/classes
   * <b>Requires at least one package/class</b>
   *
   * @param changeLogsScanPackageList package to be scanned
   * @return builder for fluent interface
   */
  BUILDER_TYPE addChangeLogsScanPackages(List<String> changeLogsScanPackageList);

  /**
   * Adds a package(or class by its full classname) to be scanned  to the list. Mongo allows multiple packages/classes
   * <b>Requires at least one package/class</b>
   *
   * @param changeLogsScanPackage package to be scanned
   * @return builder for fluent interface
   */
  default BUILDER_TYPE addChangeLogsScanPackage(String changeLogsScanPackage) {
    return addChangeLogsScanPackages(Collections.singletonList(changeLogsScanPackage));

  }

  /**
   * Adds a class to be scanned  to the list. Mongo allows multiple packages/classes
   * <b>Requires at least one package/class</b>
   *
   * @param clazz package to be scanned
   * @return builder for fluent interface
   */
  BUILDER_TYPE addChangeLogClass(Class clazz);

}
