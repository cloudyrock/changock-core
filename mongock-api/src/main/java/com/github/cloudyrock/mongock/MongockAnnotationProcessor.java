package com.github.cloudyrock.mongock;

import java.lang.reflect.Method;

public class MongockAnnotationProcessor extends MongockAnnotationProcessorBase<ChangeSetItem> {
  
  @Override
  protected ChangeSetItem createChangeSetItemInstance(String id, String author, String order, boolean runAlways, String systemVersion, boolean failFast, Method method, Method rollbackMethod, boolean beforeChangeSets, boolean afterChangeSets) {
    return new ChangeSetItem(id, author, order, runAlways, systemVersion, failFast, method, rollbackMethod, beforeChangeSets, afterChangeSets);
  }
}
