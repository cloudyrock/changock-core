package io.changock.runner.core.builder;

import io.changock.migration.api.config.ChangockConfiguration;

import java.util.Collections;
import java.util.List;

public interface PackageBuilderConfigurable<BUILDER_TYPE extends RunnerBuilderConfigurable, CONFIG extends ChangockConfiguration> {


  BUILDER_TYPE setConfig(CONFIG config);

  /**
   * Adds a list of packages to be scanned  to the list. Mongock allows multiple classes and packages
   * Mongo allows multiple packages
   * <b>Requires at least one package</b>
   *
   * @param changeLogsScanPackageList list of packages to be scanned
   * @return builder for fluent interface
   */
  BUILDER_TYPE addChangeLogsScanPackages(List<String> changeLogsScanPackageList);

  /**
   * Adds a package to be scanned  to the list. Mongock allows multiple classes and packages
   * <b>Requires at least one package</b>
   *
   * @param changeLogsScanPackage package to be scanned
   * @return builder for fluent interface
   */
  default BUILDER_TYPE addChangeLogsScanPackage(String changeLogsScanPackage) {
    return addChangeLogsScanPackages(Collections.singletonList(changeLogsScanPackage));

  }

  /**
   * Adds a list of classes to be scanned  to the list. Mongock allows multiple classes and packages
   * Mongo allows multiple packages
   * <b>Requires at least one package</b>
   *
   * @param classes list of classes to be scanned
   * @return builder for fluent interface
   */
  BUILDER_TYPE addChangeLogClasses(List<Class<?>> classes);

  /**
   * Adds a class to be scanned  to the list. Mongock allows multiple classes and packages
   * <b>Requires at least one package</b>
   *
   * @param clazz package to be scanned
   * @return builder for fluent interface
   */
  default BUILDER_TYPE addChangeLogClass(Class<?> clazz) {
    return addChangeLogClasses(Collections.singletonList(clazz));
  }

}
