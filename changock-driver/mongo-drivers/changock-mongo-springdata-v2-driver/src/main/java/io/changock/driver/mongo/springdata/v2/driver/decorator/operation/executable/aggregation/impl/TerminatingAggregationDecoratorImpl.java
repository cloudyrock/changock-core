package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.TerminatingAggregationDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class TerminatingAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.TerminatingAggregation<T>>
    implements TerminatingAggregationDecorator<T> {
  public TerminatingAggregationDecoratorImpl(ExecutableAggregationOperation.TerminatingAggregation<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
