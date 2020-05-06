package io.changock.runner.core;

class DependencyInjectionException extends RuntimeException {

  public static DependencyInjectionException parameterNotInjected(String method, String parameterType) {
    return new DependencyInjectionException(method, parameterType);
  }

  public static DependencyInjectionException parameterNotAllowed(String method, String parameterType, String replacementType) {
    return new DependencyInjectionException(method, parameterType, replacementType);
  }


  private DependencyInjectionException(String method, String parameterType) {
    super(String.format("Method[%s] using parameter[%s] not injected", method, parameterType));
  }

  private DependencyInjectionException(String method, String parameterType, String replacementType) {
    super(String.format("Method[%s] using NOT ALLOWED parameter[%s]. Please replace with %s", method, parameterType, replacementType));
  }



}
