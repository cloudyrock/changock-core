package io.changock.driver.api.common;

public class DependencyInjectionException extends RuntimeException {

  private final Class wrongParameter;
  private final String name;

  public DependencyInjectionException(Class wrongParameter) {
    this(wrongParameter, null);
  }

  public DependencyInjectionException(Class wrongParameter, String name) {
    this.wrongParameter = wrongParameter;
    this.name = name;
  }

  public Class getWrongParameter() {
    return wrongParameter;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder("Wrong parameter[")
        .append(getWrongParameter().getSimpleName())
        .append("]");
    if(name != null) {
      sb.append(" with name: ")
          .append(name);
    }
    return sb.toString();
  }
}
