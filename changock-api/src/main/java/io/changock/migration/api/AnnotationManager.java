package io.changock.migration.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public interface AnnotationManager {

  Collection<Class<? extends Annotation>> getChangeLogAnnotationClass();

  boolean isChangeSetAnnotated(Method method);

  String getChangeLogOrder(Class<?> type);

  ChangeSetItem getChangeSet(Method method);
}
