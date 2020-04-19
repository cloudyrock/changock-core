package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.MapReduceWithProjectionDecorator;
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
