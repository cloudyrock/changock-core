package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.AggregationWithAggregationDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class AggregationWithAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.AggregationWithAggregation<T>>
    implements AggregationWithAggregationDecorator<T> {

  public AggregationWithAggregationDecoratorImpl(ExecutableAggregationOperation.AggregationWithAggregation<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}