package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.ExecutableFindDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class ExecutableFindDecoratorImpl<T> extends DecoratorBase<ExecutableFindOperation.ExecutableFind<T>> implements ExecutableFindDecorator<T> {

  public ExecutableFindDecoratorImpl(ExecutableFindOperation.ExecutableFind<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
