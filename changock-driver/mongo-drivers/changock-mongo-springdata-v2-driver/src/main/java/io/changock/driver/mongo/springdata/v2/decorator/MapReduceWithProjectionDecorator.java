package io.changock.driver.mongo.springdata.v2.decorator;

import io.changock.driver.mongo.springdata.v2.decorator.impl.MapReduceWithQueryDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithProjectionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithProjection<T>, MapReduceWithQueryDecorator<T> {

    @Override
    ExecutableMapReduceOperation.MapReduceWithProjection<T> getImpl();

    @Override
    default  <R> ExecutableMapReduceOperation.MapReduceWithQuery<R> as(Class<R> resultType) {
        return getInvoker().invoke(()-> new MapReduceWithQueryDecoratorImpl<>(getImpl().as(resultType), getInvoker()));
    }
}
