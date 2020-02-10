package io.changock.runner.core;

class DependencyInjectionException extends RuntimeException {

  private final String method;
  private final String parameterType;

  DependencyInjectionException(String method, String parameterType) {
    this.method = method;
    this.parameterType = parameterType;
  }

  String getMethod() {
    return method;
  }

  String getParameterType() {
    return parameterType;
  }
}
