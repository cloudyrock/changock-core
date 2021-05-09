package com.github.cloudyrock.springboot.v2_2.base.util;


public interface InterfaceDependency {

  default String getValue() {
    return "value";
  }

  default InterfaceDependency getInstance() {
    return new InterfaceDependencyImpl();
  }

}
