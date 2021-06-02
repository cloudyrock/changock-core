package com.github.cloudyrock.mongock;

import java.lang.reflect.Method;
import java.util.Objects;

public class ChangeSetItem {

  private final String id;

  private final String author;

  private final String order;

  private final boolean runAlways;

  private final String systemVersion;

  private final Method method;

  private final boolean failFast;

  private final boolean preMigration;

  private final boolean postMigration;


  public ChangeSetItem(String id,
                       String author,
                       String order,
                       boolean runAlways,
                       String systemVersion,
                       boolean  failFast,
                       Method method) {
    this(id, author, order, runAlways, systemVersion, failFast, false, false, method);
  }

  public ChangeSetItem(String id,
                       String author,
                       String order,
                       boolean runAlways,
                       String systemVersion,
                       boolean  failFast,
                       boolean preMigration,
                       boolean postMigration,
                       Method method) {
    checkParameters(preMigration, postMigration);
    this.id = id;
    this.author = author;
    this.order = order;
    this.runAlways = runAlways;
    this.systemVersion = systemVersion;
    this.method = method;
    this.failFast = failFast;
    this.preMigration = preMigration;
    this.postMigration = postMigration;
  }

  private static void checkParameters(boolean preMigration, boolean postMigration) {
    if (preMigration && postMigration) {
      throw new IllegalArgumentException("A ChangeSetItem can't be defined to be executed pre and post migration.");
    }
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

  public boolean isPreMigration() {
    return preMigration;
  }

  public boolean isPostMigration() {
    return postMigration;
  }

  public boolean isMigration() {
    return !isPreMigration() && !isPostMigration();
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
