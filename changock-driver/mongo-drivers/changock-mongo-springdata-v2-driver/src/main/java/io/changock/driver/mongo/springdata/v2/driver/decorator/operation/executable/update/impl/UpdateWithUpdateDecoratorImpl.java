package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.UpdateWithUpdateDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class UpdateWithUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.UpdateWithUpdate<T>> implements UpdateWithUpdateDecorator<T> {

  public UpdateWithUpdateDecoratorImpl(ExecutableUpdateOperation.UpdateWithUpdate<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
