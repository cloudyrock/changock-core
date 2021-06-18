package com.github.cloudyrock.mongock;

import java.lang.reflect.Method;

public class MongockAnnotationProcessor implements AnnotationProcessor<ChangeSetItem> {


  @Override
  public boolean isMethodAnnotatedAsChange(Method method) {
    return method.isAnnotationPresent(ChangeSet.class);
  }



  @Override
  public ChangeSetItem getChangePerformerItem(Method changeSetMethod, Method rollbackMethod) {
    ChangeSet ann = changeSetMethod.getAnnotation(ChangeSet.class);
    return new ChangeSetItem(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), ann.failFast(), changeSetMethod, rollbackMethod);
  }

}
