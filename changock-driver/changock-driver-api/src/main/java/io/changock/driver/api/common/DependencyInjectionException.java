package io.changock.driver.api.common;

public class DependencyInjectionException extends RuntimeException {

  private final Class wrongParameter;

  public DependencyInjectionException(Class wrongParameter) {
    this.wrongParameter = wrongParameter;
  }

  public Class getWrongParameter() {
    return wrongParameter;
  }

  @Override
  public String getMessage() {
    return String.format("Wrong parameter[%s]", getWrongParameter().getSimpleName());
  }
}
