package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl.AggregationWithAggregationDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public interface AggregationWithCollectionDecorator<T> extends Invokable, ExecutableAggregationOperation.AggregationWithCollection<T> {

  ExecutableAggregationOperation.AggregationWithCollection<T> getImpl();

  @Override
  default ExecutableAggregationOperation.AggregationWithAggregation<T> inCollection(String collection) {
    return new AggregationWithAggregationDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
