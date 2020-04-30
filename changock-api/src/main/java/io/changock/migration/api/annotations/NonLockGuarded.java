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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NonLockGuarded {


  /**
   * Indicates the grade of non-lock-guard.
   * @return type
   */
  Type[] type() default {Type.METHOD};


  enum Type {
    /**
     * Indicates the returned object shouldn't be decorated for lock guard. So clean instance is returned.
     * But still the method needs to bbe lock-guarded
     */
    RETURN,

    /**
     * Indicates the method shouldn't be lock-guarded, but still should decorate the returned object(if applies)
     */
    METHOD,

    /**
     * Indicates the method shouldn't be lock-guarded neither the returned object should be decorated for lock guard.
     */
    NONE
  }

}
