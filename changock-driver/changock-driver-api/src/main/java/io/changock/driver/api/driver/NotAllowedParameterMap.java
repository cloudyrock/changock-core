package io.changock.driver.api.driver;

import java.util.Objects;

public class ParameterReplacement {

  private final Class parameterNotAllowed;
  private final Class parameterReplacement;

  public ParameterReplacement(Class parameterNotAllowed, Class parameterReplacement) {
    this.parameterNotAllowed = parameterNotAllowed;
    this.parameterReplacement = parameterReplacement;
  }


  public Class getParameterNotAllowed() {
    return parameterNotAllowed;
  }

  public Class getParameterReplacement() {
    return parameterReplacement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParameterReplacement that = (ParameterReplacement) o;
    return Objects.equals(parameterNotAllowed, that.parameterNotAllowed) &&
        Objects.equals(parameterReplacement, that.parameterReplacement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parameterNotAllowed, parameterReplacement);
  }

  @Override
  public String toString() {
    return "ParameterReplacement{" +
        "parameterNotAllowed=" + parameterNotAllowed +
        ", parameterReplacement=" + parameterReplacement +
        '}';
  }
}
