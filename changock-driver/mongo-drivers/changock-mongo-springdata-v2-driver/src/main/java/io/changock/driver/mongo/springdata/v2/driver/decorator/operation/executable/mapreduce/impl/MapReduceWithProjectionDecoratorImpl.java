package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.MapReduceWithProjectionDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithProjectionDecoratorImpl<T> implements MapReduceWithProjectionDecorator<T> {

    private final ExecutableMapReduceOperation.MapReduceWithProjection<T> impl;
    private final MethodInvoker invoker;

    public MapReduceWithProjectionDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithProjection<T> impl, MethodInvoker methodInvoker)  {
        this.impl = impl;
        this.invoker = methodInvoker;
    }
    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }

    @Override
    public ExecutableMapReduceOperation.MapReduceWithProjection<T> getImpl() {
        return impl;
    }
}
