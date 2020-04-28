package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation;

import io.changock.driver.mongo.springdata.v2.driver.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public interface ExecutableAggregationDecorator<T> extends
    Invokable,
    ExecutableAggregationOperation.ExecutableAggregation<T>,
    AggregationWithCollectionDecorator<T>,
    AggregationWithAggregationDecorator<T> {

  ExecutableAggregationOperation.ExecutableAggregation<T> getImpl();
}
