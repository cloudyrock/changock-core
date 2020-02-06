package io.changock.runner.base.builder;

import java.util.Map;

public interface RunnerBuilderConfigurable<BUILDER_TYPE extends RunnerBuilderConfigurable> extends PackageBuilderConfigurable<BUILDER_TYPE> {


  /**
   * Feature which enables/disables throwing MongockException if the lock cannot be obtained
   *
   * @param throwExceptionIfCannotObtainLock Mongock will throw MongockException if lock can not be obtained
   * @return builder for fluent interface
   */
  BUILDER_TYPE setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock);


  /**
   * Feature which enables/disables execution
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return builder for fluent interface
   */
  BUILDER_TYPE setEnabled(boolean enabled);

  /**
   * Set up the lock with minimal configuration. This implies Mongock will throw an exception if cannot obtains the lock.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return builder for fluent interface
   */
  BUILDER_TYPE setLockConfig(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries);

  /**
   * Set up the lock with default configuration to wait for it and through an exception when cannot obtain it.
   *
   * @return builder for fluent interface
   */
  BUILDER_TYPE setDefaultLock();

  /**
   * Set up the start Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagget with systemVersion inside that
   * range will be applied
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
   * range will be applied
   *
   * @param endSystemVersion Version to end with
   * @return builder for fluent interface
   */
  BUILDER_TYPE setEndSystemVersion(String endSystemVersion);

  /**
   * Set the metadata for the mongock process. This metadata will be added to each document in the mongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   *
   * @param metadata Custom metadata object  to be added
   * @return builder for fluent interface
   */
  BUILDER_TYPE withMetadata(Map<String, Object> metadata);

//  MONGOCK_TYPE build();
}
