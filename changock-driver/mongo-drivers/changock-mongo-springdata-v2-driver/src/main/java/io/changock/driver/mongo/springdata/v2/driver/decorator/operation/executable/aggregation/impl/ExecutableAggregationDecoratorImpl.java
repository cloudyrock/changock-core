package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.ExecutableAggregationDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class ExecutableAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.ExecutableAggregation<T>>
    implements ExecutableAggregationDecorator<T> {
  public ExecutableAggregationDecoratorImpl(ExecutableAggregationOperation.ExecutableAggregation<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
