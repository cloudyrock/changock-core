package com.github.cloudyrock.mongock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class<?> containing particular changesets (@{@link ChangeSet})
 *
 *
 * @see ChangeSet
 * @since 27/07/2014
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeLog {
  /**
   * Sequence that provide an order for changelog classes.
   * If not set, then canonical name of the class is taken and sorted alphabetically, ascending.
   *
   * @return order
   */
  String order() default "";
  
    /**
   * If true, will make the entire migration to break if the changeLog produce an exception or the validation doesn't
   * success. Migration will continue otherwise.
   * @return failFast
   */
  boolean failFast() default true;
}
