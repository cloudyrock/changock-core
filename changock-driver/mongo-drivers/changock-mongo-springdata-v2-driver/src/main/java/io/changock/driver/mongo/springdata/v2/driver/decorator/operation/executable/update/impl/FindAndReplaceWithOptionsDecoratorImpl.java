package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.FindAndReplaceWithOptionsDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndReplaceWithOptionsDecoratorImpl<T>
    extends DecoratorBase<ExecutableUpdateOperation.FindAndReplaceWithOptions<T>>
    implements FindAndReplaceWithOptionsDecorator<T> {

  public FindAndReplaceWithOptionsDecoratorImpl(ExecutableUpdateOperation.FindAndReplaceWithOptions<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
