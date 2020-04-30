package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.UpdateWithUpdateDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class UpdateWithUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.UpdateWithUpdate<T>> implements UpdateWithUpdateDecorator<T> {

  public UpdateWithUpdateDecoratorImpl(ExecutableUpdateOperation.UpdateWithUpdate<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
