package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.aggregation;

import io.changock.driver.mongo.springdata.v3.driver.decorator.impl.CloseableIteratorDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.util.CloseableIterator;

public interface TerminatingAggregationDecorator<T> extends Invokable, ExecutableAggregationOperation.TerminatingAggregation<T> {

  ExecutableAggregationOperation.TerminatingAggregation<T> getImpl();

  @Override
  default AggregationResults<T> all() {
    return getInvoker().invoke(()-> getImpl().all());
  }

  @Override
  default CloseableIterator<T> stream() {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(()-> getImpl().stream()), getInvoker());
  }
}
