package com.github.cloudyrock.mongock.runner.core.util;


import com.github.cloudyrock.mongock.NonLockGuarded;

public interface InterfaceDependency {

  default String getValue() {
    return "value";
  }

  default InterfaceDependency getInstance() {
    return new InterfaceDependencyImpl();
  }

  @NonLockGuarded
  default String getNonLockguardedValue() {
    return "value";
  }

}
