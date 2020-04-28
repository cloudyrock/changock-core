package io.changock.driver.mongo.springdata.v2.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.MongoDbFactoryDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.MongoDbFactory;

@Deprecated
public class MongoDbFactoryDecoratorImpl extends DecoratorBase<MongoDbFactory> implements MongoDbFactoryDecorator {

  public MongoDbFactoryDecoratorImpl(MongoDbFactory impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
