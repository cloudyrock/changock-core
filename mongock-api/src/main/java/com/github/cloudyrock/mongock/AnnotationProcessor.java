package com.github.cloudyrock.mongock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public interface AnnotationProcessor {

  Collection<Class<? extends Annotation>> getChangeLogAnnotationClass();

  boolean isMethodAnnotatedAsChange(Method method);

  String getChangeLogOrder(Class<?> type);

  boolean getChangeLogFailFast(Class<?> type);

  boolean isPreMigration(Class<?> type);

  boolean isPostMigration(Class<?> type);

  /**
   * This methods return the metatada for a method that is annootated with a moncok annotation that
   * implies a change. ChangetSet, validation, undo, etc.
   * @param method
   * @return
   */
  ChangeSetItem getChangePerformerItem(Method method);
}
