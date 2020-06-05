package io.changock.driver.api.driver;

import io.changock.migration.api.exception.ChangockException;

import java.util.Objects;

public class ChangeSetDependency {

  private static final String DEFAULT_NAME = "default_name_not_used";

  private String name;
  private Class<?> type;
  private Object instance;


  public ChangeSetDependency(Object instance) {
    this(instance.getClass(), instance);
  }

  public ChangeSetDependency(Class<?> type, Object instance) {
    this(DEFAULT_NAME, type, instance);
  }

  public ChangeSetDependency(String name, Class<?> type, Object instance) {
    checkParameters(name, type, instance);
    this.name = name;
    this.type = type;
    this.instance = instance;
  }

  private void checkParameters(String name, Class<?> type, Object instance) {
    if(name == null || name.isEmpty()) {
      throw new ChangockException("dependency name cannot be null/empty");
    }
    if(type ==null) {
      throw new ChangockException("dependency type cannot be null");
    }
    if(instance ==null) {
      throw new ChangockException("dependency instance cannot be null");
    }
  }

  public String getName() {
    return name;
  }

  public Class<?> getType() {
    return type;
  }

  public Object getInstance() {
    return instance;
  }

  public boolean isDefaultNamed() {
    return DEFAULT_NAME.equals(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChangeSetDependency)) return false;
    ChangeSetDependency that = (ChangeSetDependency) o;
    return name.equals(that.name) && type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }
}
