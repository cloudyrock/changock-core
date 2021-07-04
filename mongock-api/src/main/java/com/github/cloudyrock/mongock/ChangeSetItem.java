package com.github.cloudyrock.mongock;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

public class ChangeSetItem {

  private final String id;

  private final String author;

  private final String order;

  private final boolean runAlways;

  private final String systemVersion;

  private final Method method;

  private final boolean failFast;
  
  private final Method rollbackMethod;
  
  private final boolean beforeChangeSets;
  
  private final boolean afterChangeSets;


  public ChangeSetItem(String id,
                       String author,
                       String order,
                       boolean runAlways,
                       String systemVersion,
                       boolean failFast,
                       Method changeSetMethod,
                       Method rollbackMethod,
                       boolean beforeChangeSets,
                       boolean afterChangeSets) {
    this.id = id;
    this.author = author;
    this.order = order;
    this.runAlways = runAlways;
    this.systemVersion = systemVersion;
    this.method = changeSetMethod;
    this.failFast = failFast;
    this.rollbackMethod = rollbackMethod;
    this.beforeChangeSets = beforeChangeSets;
    this.afterChangeSets = afterChangeSets;
  }


  public String getId() {
    return id;
  }

  public String getAuthor() {
    return author;
  }

  public String getOrder() {
    return order;
  }

  public boolean isRunAlways() {
    return runAlways;
  }

  public String getSystemVersion() {
    return systemVersion;
  }

  public Method getMethod() {
    return method;
  }

  public boolean isFailFast() {
    return failFast;
  }

  public Optional<Method> getRollbackMethod() {
    return Optional.ofNullable(rollbackMethod);
  }
  
  public boolean isBeforeChangeSets() {
    return beforeChangeSets;
  }

  public boolean isAfterChangeSets() {
    return afterChangeSets;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeSetItem that = (ChangeSetItem) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ChangeSetItem{" +
        "id='" + id + '\'' +
        ", author='" + author + '\'' +
        ", order='" + order + '\'' +
        ", runAlways=" + runAlways +
        ", systemVersion='" + systemVersion + '\'' +
        ", method=" + method +
        ", failFast=" + failFast +
        ", beforeChangeSets=" + beforeChangeSets +
        ", afterChangeSets=" + afterChangeSets +
        '}';
  }

  public String toPrettyString() {
    return "ChangeEntry{" +
        "\"id\"=\"" + id + "\"" +
        ", \"author\"=\"" + author + "\"" +
        ", \"class\"=\"" + method.getDeclaringClass().getSimpleName() + "\"" +
        ", \"method\"=\"" + method.getName() + "\"" +
        '}';
  }


}
