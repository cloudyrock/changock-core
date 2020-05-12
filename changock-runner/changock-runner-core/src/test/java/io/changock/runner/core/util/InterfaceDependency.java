package io.changock.runner.core.util;


public interface InterfaceDependency {

  default String getValue() {
    return "value";
  }

  default InterfaceDependency getInstance() {
    return new InterfaceDependencyImpl();
  }

}
