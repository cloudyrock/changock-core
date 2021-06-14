package com.github.cloudyrock.mongock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public interface AnnotationProcessor {

  Collection<Class<? extends Annotation>> getChangeLogAnnotationClass();

  boolean isMethodAnnotatedAsChange(Method method);

  String getChangeLogOrder(Class<?> type);

  boolean isFailFast(Class<?> changeLogClass);

  boolean isPreMigration(Class<?> changeLogClass);

  boolean isPostMigration(Class<?> changeLogClass);

  /**
   * Returns the metatada associated to a method via a mongock change annotation, which includes
   * : ChangetSet, validation, undo, etc.
   * @param changeSetMethod
   * @return The metadata associated to a change method
   */
  ChangeSetItem getChangePerformerItem(Method changeSetMethod);
}
