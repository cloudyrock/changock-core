package com.github.cloudyrock.mongock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

public class MongockAnnotationProcessor implements AnnotationProcessor {

  @Override
  public Collection<Class<? extends Annotation>> getChangeLogAnnotationClass() {
    return Collections.singletonList(ChangeLog.class);
  }

  @Override
  public boolean isMethodAnnotatedAsChange(Method method) {
    return method.isAnnotationPresent(ChangeSet.class);
  }

  @Override
  public String getChangeLogOrder(Class<?> type) {
    return type.getAnnotation(ChangeLog.class).order();
  }

  @Override
  public boolean getChangeLogFailFast(Class<?> type) {
    return type.getAnnotation(ChangeLog.class).failFast();
  }

  @Override
  public boolean isPreMigration(Class<?> type) {
    return type.isAnnotationPresent(PreMigration.class);
  }

  @Override
  public boolean isPostMigration(Class<?> type) {
    return type.isAnnotationPresent(PostMigration.class);
  }

  @Override
  public ChangeSetItem getChangePerformerItem(Method method) {
    ChangeSet ann = method.getAnnotation(ChangeSet.class);
    return new ChangeSetItem(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), ann.failFast(), method);
  }
}
