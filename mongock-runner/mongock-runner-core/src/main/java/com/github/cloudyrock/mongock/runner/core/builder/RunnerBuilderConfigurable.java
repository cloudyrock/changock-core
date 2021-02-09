package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import java.util.function.Function;

import java.util.Map;

public interface RunnerBuilderConfigurable<BUILDER_TYPE extends RunnerBuilderConfigurable, CONFIG extends MongockConfiguration>
    extends PackageBuilderConfigurable<BUILDER_TYPE, CONFIG> {

  /**
   * Sets a function that will be used to instantiate ChangeLog classes.
   * If unset, Class.getConstructor().newInstance() will be used
   *
   * @param changeLogInstantiator the function that will create an instance of a class
   * @return builder for fluent interface
   */
  BUILDER_TYPE setChangeLogInstantiator(Function<Class, Object> changeLogInstantiator);

  /**
   * Adds a legacy migration to be executed before the actual migration
   * @param legacyMigration represents the legacy migration
   * @return builder for fluent interface
   */
  BUILDER_TYPE setLegacyMigration(LegacyMigration legacyMigration);

  /**
   * Feature which enables/disables execution
   * <b>Optional</b> Default value true.
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return builder for fluent interface
   */
  BUILDER_TYPE setEnabled(boolean enabled);

  /**
   * Indicates if the ignored changeSets should be tracked or not
   *
   * @param trackIgnored if the ignored changeSets should be tracked
   * @return builder for fluent interface
   */
  BUILDER_TYPE setTrackIgnored(boolean trackIgnored);

  /**
   * Indicates that in case the lock cannot be obtained, therefore the migration is not executed, Mongock won't throw
   * any exception and the application will carry on.
   *
   * Only set this to false if the changes are not mandatory and the application can work without them. Leave it true otherwise.
   * <b>Optional</b> Default value true.
   *
   * @return builder for fluent interface
   */
  BUILDER_TYPE dontFailIfCannotAcquireLock();


  /**
   * Set up the start Version for versioned schema changes.
   * This shouldn't be confused with a supposed change version(Notice, currently changeSet doesn't have version).
   * This is from a consultancy point of view. So the changeSet are tagged with a systemVersion and then when building
   * Mongock, you specify the systemVersion range you want to apply, so all the changeSets tagged with systemVersion
   * inside that range will be applied
   * <b>Optional</b> Default value 0
   *
   * @param startSystemVersion Version to start with
   * @return builder for fluent interface
   */
  BUILDER_TYPE setStartSystemVersion(String startSystemVersion);

  /**
   * Set up the end Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagged with systemVersion inside that
   * range will be applied.
   * <b>Optional</b> Default value string value of MAX_INTEGER
   *
   * @param endSystemVersion Version to end with
   * @return builder for fluent interface
   */
  BUILDER_TYPE setEndSystemVersion(String endSystemVersion);

  /**
   * Set the metadata for the Mongock process. This metadata will be added to each document in the MongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   * <b>Optional</b> Default value empty Map
   *
   * @param metadata Custom metadata object  to be added
   * @return builder for fluent interface
   */
  BUILDER_TYPE withMetadata(Map<String, Object> metadata);

  /**
   * Manually adds a dependency to be used in changeLogs, which can be retrieved by its own type
   * @param instance dependency
   * @return builder for fluent interface
   */
  default BUILDER_TYPE addDependency(Object instance) {
    return addDependency(instance.getClass(), instance);
  }

  /**
   * Manually adds a dependency to be used in changeLogs, which can be retrieved by a name
   * @param name name for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  default BUILDER_TYPE addDependency(String name, Object instance) {
    return addDependency(name, instance.getClass(), instance);
  }

  /**
   * Manually adds a dependency to be used in changeLogs, which can be retrieved by a type
   * @param type type for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  default BUILDER_TYPE addDependency(Class type, Object instance) {
    return addDependency(ChangeSetDependency.DEFAULT_NAME, type, instance);
  }

  /**
   * Manually adds a dependency to be used in changeLogs, which can be retrieved by a type or name
   * @param name name for which it should be searched by
   * @param type type for which it should be searched by
   * @param instance dependency
   * @return builder for fluent interface
   */
  BUILDER_TYPE addDependency(String name, Class type, Object instance);
}
