package io.changock.driver.api.common;

public class NamedDependencyInjectionException extends RuntimeException {

  private final String dependencyName;

  public NamedDependencyInjectionException(String dependencyName) {
    this.dependencyName = dependencyName;
  }

  public String getDependencyName() {
    return dependencyName;
  }

  @Override
  public String getMessage() {
    return String.format("Wrong parameter with name[%s]", dependencyName);
  }
}
