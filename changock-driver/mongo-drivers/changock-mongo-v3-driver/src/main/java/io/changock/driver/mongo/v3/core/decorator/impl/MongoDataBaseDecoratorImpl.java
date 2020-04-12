package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.MongoDatabaseDecorator;

public class MongoDataBaseDecoratorImpl implements MongoDatabaseDecorator {
  private final MongoDatabase impl;
  private final MethodInvoker invoker;

  public MongoDataBaseDecoratorImpl(MongoDatabase implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.invoker = lockerCheckInvoker;
  }

  public MongoDatabase getImpl() {
    return impl;
  }

  public MethodInvoker getInvoker() {
    return invoker;
  }

}
