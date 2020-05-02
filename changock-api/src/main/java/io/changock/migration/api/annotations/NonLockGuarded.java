package io.changock.migration.api.annotations;

import io.changock.migration.api.annotations.ChangeLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set of changes to be added to the DB. Many changesets are included in one changelog.
 *
 *
 * @see ChangeLog
 * @since 27/07/2014
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonLockGuarded {


  /**
   * Indicates the grade of non-lock-guard.
   * @return value
   */
  NonLockGuardedType[] value() default {NonLockGuardedType.METHOD};



}
