package io.changock.driver.mongo.springdata.v3.driver.decorator.impl;

import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.springdata.v3.driver.decorator.MongoDatabaseFactoryDecorator;
import org.springframework.data.mongodb.MongoDatabaseFactory;

public class MongoDatabaseFactoryDecoratorImpl extends DecoratorBase<MongoDatabaseFactory> implements MongoDatabaseFactoryDecorator {

  public MongoDatabaseFactoryDecoratorImpl(MongoDatabaseFactory impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
