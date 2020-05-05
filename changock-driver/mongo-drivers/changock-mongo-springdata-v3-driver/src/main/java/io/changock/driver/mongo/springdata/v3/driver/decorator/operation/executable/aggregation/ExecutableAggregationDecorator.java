package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.aggregation;

import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public interface ExecutableAggregationDecorator<T> extends
    Invokable,
    ExecutableAggregationOperation.ExecutableAggregation<T>,
    AggregationWithCollectionDecorator<T>,
    AggregationWithAggregationDecorator<T> {

  ExecutableAggregationOperation.ExecutableAggregation<T> getImpl();
}
