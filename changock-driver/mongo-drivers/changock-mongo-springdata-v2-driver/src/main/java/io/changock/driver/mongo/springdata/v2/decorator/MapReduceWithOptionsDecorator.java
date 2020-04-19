package io.changock.driver.mongo.springdata.v2.decorator;

import io.changock.driver.core.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.impl.ExecutableMapReduceDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;

public interface MapReduceWithOptionsDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithOptions<T> {

    ExecutableMapReduceOperation.MapReduceWithOptions<T> getImpl();

    MethodInvoker getInvoker();

    //TODO implement
    @Override
    default ExecutableMapReduceOperation.ExecutableMapReduce<T> with(MapReduceOptions options) {
        return getInvoker().invoke(() -> new ExecutableMapReduceDecoratorImpl<>(getImpl().with(options), getInvoker()));
    }
}
