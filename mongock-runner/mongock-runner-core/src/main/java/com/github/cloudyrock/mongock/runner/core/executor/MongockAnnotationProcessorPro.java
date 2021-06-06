package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.PostMigration;
import com.github.cloudyrock.mongock.PreMigration;
import com.github.cloudyrock.mongock.pro.ChangeSetItemPro;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

public class MongockAnnotationProcessorPro implements AnnotationProcessor {

  @Override
  public Collection<Class<? extends Annotation>> getChangeLogAnnotationClass() {
    return Collections.singletonList(ChangeLog.class);
  }

  @Override
  public boolean isChangeSetAnnotated(Method method) {
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
  public boolean getChangeLogPreMigration(Class<?> type) {
    return type.isAnnotationPresent(PreMigration.class);
  }

  @Override
  public boolean getChangeLogPostMigration(Class<?> type) {
    return type.isAnnotationPresent(PostMigration.class);
  }

  @Override
  public ChangeSetItemPro getChangeSet(Method method) {
    ChangeSet ann = method.getAnnotation(ChangeSet.class);
    return new ChangeSetItemPro(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), ann.failFast(), method);
  }
}
