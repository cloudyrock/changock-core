package io.changock.test.util.decorator;

import java.util.HashMap;

public class DecoratorTestCollection extends HashMap<Class<?>, DecoratorDefinition> {


  public <T> DecoratorTestCollection addDecorator(Class<T> interfaceType, Class<? extends T> implementingClass, String... noLockGardMethods) {
    this.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass, noLockGardMethods));
    return this;
  }

  public <T> DecoratorTestCollection addDecorator(Class<T> interfaceType, Class<? extends T> implementingClass) {
    this.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass));
    return this;
  }

  public <T, R extends T> DecoratorTestCollection addDecorator(Class<T> interfaceType, Class<R> implementingClass, R instance, String... noLockGardMethods) {
    this.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass, instance, noLockGardMethods));
    return this;
  }

}
