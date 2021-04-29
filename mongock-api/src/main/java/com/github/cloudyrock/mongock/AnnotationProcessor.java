package com.github.cloudyrock.mongock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public interface AnnotationProcessor {

  Collection<Class<? extends Annotation>> getChangeLogAnnotationClass();

  boolean isChangeSetAnnotated(Method method);

  String getChangeLogOrder(Class<?> type);
  
  boolean getChangeLogFailFast(Class<?> type);
  
  boolean getChangeLogPreMigration(Class<?> type);
  
  boolean getChangeLogPostMigration(Class<?> type);

  ChangeSetItem getChangeSet(Method method);
}
