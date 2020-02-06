package io.changock.driver.api.changelog;

import java.lang.reflect.Method;

public class ChangeSetItem {

  private String id;

  private String author;

  private String order;

  private  boolean runAlways;

  private String systemVersion;

  private Method method;

  public ChangeSetItem(String id, String author, String order, boolean runAlways, String systemVersion, Method method) {
    this.id = id;
    this.author = author;
    this.order = order;
    this.runAlways = runAlways;
    this.systemVersion = systemVersion;
    this.method = method;
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

  @Override
  public String toString() {
    return "ChangeSetItem{" +
        "id='" + id + '\'' +
        ", author='" + author + '\'' +
        ", order='" + order + '\'' +
        ", runAlways=" + runAlways +
        ", systemVersion='" + systemVersion + '\'' +
        '}';
  }
}
