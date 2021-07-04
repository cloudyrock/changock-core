package com.github.cloudyrock.mongock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set of changes inside a changelog that will be performed as the last stage, before the proper migration.
 * The motivation for this is to be able to perform some operations that need to be executed out of the transaction,
 * when transaction strategy is CHANGELOG
 *
 * @see com.github.cloudyrock.mongock.config.TransactionStrategy
 *
 * @see ChangeLog
 * @since 27/07/2014
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterChangeSets {

  /**
   * Author of the post-transaction.
   * Obligatory
   *
   * @return author
   */
  String author();  // must be set

  /**
   * Unique ID of the post-transaction.
   * Obligatory
   *
   * @return unique id
   */
  String id();      // must be set

  /**
   * Sequence that provide correct order for post-transactions. Sorted alphabetically, ascending.
   * Obligatory.
   *
   * @return ordering
   */
  String order();   // must be set

  /**
   * Executes the change set on every Mongock's execution, even if it has been run before.
   * Optional (default is false)
   *
   * @return should run always?
   */
  boolean runAlways() default false;

  /**
   * Specifies the software systemVersion on which the post-transaction is to be applied.
   * Optional (default is 0 and means all)
   *
   * @return systemVersion
   */
  String systemVersion() default "0";

  /**
   * If true, will make the entire migration to break if the post-transaction produce an exception or the validation doesn't
   * success. Migration will continue otherwise.
   *
   * @return failFast
   */
  boolean failFast() default true;

}
