package io.changock.driver.api.driver;

import java.util.Objects;

public class ChangeSetDependency {

  private Class<?> type;
  private Object instance;

  public ChangeSetDependency(Class<?> type, Object instance) {
    this.type = type;
    this.instance = instance;
  }

  public ChangeSetDependency(Object instance) {
    this.type = instance.getClass();
    this.instance = instance;
  }


  public Class<?> getType() {
    return type;
  }

  public Object getInstance() {
    return instance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeSetDependency that = (ChangeSetDependency) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
