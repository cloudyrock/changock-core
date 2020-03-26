package io.changock.driver.api.entry;

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
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

  String value();

}
