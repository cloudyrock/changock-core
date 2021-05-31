package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.config.MongockConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface ChangeLogScanner<SELF extends ChangeLogScanner<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {


  /**
   * Adds a list of packages to be scanned  to the list. Mongock allows multiple classes and packages
   * Mongock allows multiple packages
   * <b>Requires at least one package</b>
   *
   * @param changeLogsScanPackageList list of packages to be scanned
   * @return builder for fluent interface
   */
  default SELF addChangeLogsScanPackages(List<String> changeLogsScanPackageList) {
    if (changeLogsScanPackageList != null) {
      getConfig().getChangeLogsScanPackage().addAll(changeLogsScanPackageList);
    }
    return getInstance();
  }


  /**
   * Adds a package to be scanned  to the list. Mongock allows multiple classes and packages
   * <b>Requires at least one package</b>
   *
   * @param changeLogsScanPackage package to be scanned
   * @return builder for fluent interface
   */
  default SELF addChangeLogsScanPackage(String changeLogsScanPackage) {
    return addChangeLogsScanPackages(Collections.singletonList(changeLogsScanPackage));
  }

  /**
   * Adds a list of classes to be scanned  to the list. Mongock allows multiple classes and packages
   * Mongock allows multiple packages
   * <b>Requires at least one package</b>
   *
   * @param classes list of classes to be scanned
   * @return builder for fluent interface
   */
  default SELF addChangeLogClasses(List<Class<?>> classes) {
    if (classes != null) {
      classes.stream().map(Class::getName).forEach(getConfig().getChangeLogsScanPackage()::add);
    }
    return getInstance();
  }

  /**
   * Adds a class to be scanned  to the list. Mongock allows multiple classes and packages
   * <b>Requires at least one package</b>
   *
   * @param clazz package to be scanned
   * @return builder for fluent interface
   */
  default SELF addChangeLogClass(Class<?> clazz) {
    return addChangeLogClasses(Collections.singletonList(clazz));
  }

  /**
   * Sets a function that will be used to instantiate ChangeLog classes.
   * If unset, Class.getConstructor().newInstance() will be used
   *
   * @param changeLogInstantiator the function that will create an instance of a class
   * @return builder for fluent interface
   */
  SELF setChangeLogInstantiator(Function<Class<?>, Object> changeLogInstantiator);
}
