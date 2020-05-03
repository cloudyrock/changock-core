package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.FindAndReplaceWithProjectionDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndReplaceWithProjectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableUpdateOperation.FindAndReplaceWithProjection<T>>
    implements FindAndReplaceWithProjectionDecorator<T> {

  public FindAndReplaceWithProjectionDecoratorImpl(ExecutableUpdateOperation.FindAndReplaceWithProjection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }



}
