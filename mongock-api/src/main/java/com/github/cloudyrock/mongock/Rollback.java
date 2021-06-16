package com.github.cloudyrock.mongock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The rollback operation associated to a changeSet
 *
 * @see ChangeSet
 * @since 27/07/2014
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rollback {

  /**
   * Unique ID of the changeset.
   * Obligatory
   *
   * @return unique id
   */
  String value();


}
