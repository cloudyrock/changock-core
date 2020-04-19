package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.MongoDbFactoryDecorator;
import org.springframework.data.mongodb.MongoDbFactory;

public class MongoDbFactoryDecoratorImpl implements MongoDbFactoryDecorator {

  private final MethodInvoker invoker;
  private final MongoDbFactory impl;

  public MongoDbFactoryDecoratorImpl(MongoDbFactory implementation, MethodInvoker invoker) {
    this.impl = implementation;
    this.invoker = invoker;
  }

  @Override
  public MongoDbFactory getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }

}
