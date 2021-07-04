package com.github.cloudyrock.mongock;

import java.lang.reflect.Method;

public abstract class MongockAnnotationProcessorBase<CHANGESET extends ChangeSetItem> implements AnnotationProcessor<CHANGESET> {

  @Override
  public CHANGESET getChangePerformerItem(Method changeSetMethod, Method rollbackMethod) {
      if(isBeforeChangeSets(changeSetMethod)) {
          return getBeforeChangeSetsItem(changeSetMethod, rollbackMethod);
      } else if(isAfterChangeSets(changeSetMethod)) {
          return getAfterChangeSetsItem(changeSetMethod, rollbackMethod);
      } else {
          return getChangeSetItem(changeSetMethod, rollbackMethod);
      }
  }

  public CHANGESET getChangeSetItem(Method method, Method rollbackMethod) {
      ChangeSet ann = method.getAnnotation(ChangeSet.class);
      return createChangeSetItemInstance(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), ann.failFast(), method, rollbackMethod, false, false);
  }

  public CHANGESET getBeforeChangeSetsItem(Method method, Method rollbackMethod) {
      BeforeChangeSets ann = method.getAnnotation(BeforeChangeSets.class);
      return createChangeSetItemInstance(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), ann.failFast(), method, rollbackMethod, true, false);
  }

  public CHANGESET getAfterChangeSetsItem(Method method, Method rollbackMethod) {
      AfterChangeSets ann = method.getAnnotation(AfterChangeSets.class);
      return createChangeSetItemInstance(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), ann.failFast(), method, rollbackMethod, false, true);
  }
    
  protected abstract CHANGESET createChangeSetItemInstance(String id,
                                                          String author,
                                                          String order,
                                                          boolean runAlways,
                                                          String systemVersion,
                                                          boolean failFast,
                                                          Method method,
                                                          Method rollbackMethod,
                                                          boolean isBeforeChangeSets,
                                                          boolean isAfterChangeSets);
  
  @Override
  public void validateChangeLogClass(Class<?> type) {
      //todo validate that there is no method with more than one annotations of (Pr, Post, changeset)
  }

}
