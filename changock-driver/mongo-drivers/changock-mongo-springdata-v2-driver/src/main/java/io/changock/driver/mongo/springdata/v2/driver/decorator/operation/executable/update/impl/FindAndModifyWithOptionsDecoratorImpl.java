package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.FindAndModifyWithOptionsDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndModifyWithOptionsDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.FindAndModifyWithOptions<T>> implements FindAndModifyWithOptionsDecorator<T> {

  public FindAndModifyWithOptionsDecoratorImpl(ExecutableUpdateOperation.FindAndModifyWithOptions<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
