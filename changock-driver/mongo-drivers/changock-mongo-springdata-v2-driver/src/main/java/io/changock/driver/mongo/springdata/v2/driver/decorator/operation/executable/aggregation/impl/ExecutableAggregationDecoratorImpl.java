package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.ExecutableAggregationDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class ExecutableAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.ExecutableAggregation<T>>
    implements ExecutableAggregationDecorator<T> {
  public ExecutableAggregationDecoratorImpl(ExecutableAggregationOperation.ExecutableAggregation<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
