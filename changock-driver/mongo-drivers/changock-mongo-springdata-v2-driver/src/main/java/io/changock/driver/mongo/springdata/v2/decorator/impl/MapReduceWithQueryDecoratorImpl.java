package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.MapReduceWithQueryDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithQueryDecoratorImpl<T> implements MapReduceWithQueryDecorator<T> {

    private final MethodInvoker invoker;
    private final ExecutableMapReduceOperation.MapReduceWithQuery<T> impl;

    public MapReduceWithQueryDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithQuery<T> impl, MethodInvoker methodInvoker) {
        this.impl = impl;
        this.invoker = methodInvoker;
    }

    @Override
    public ExecutableMapReduceOperation.MapReduceWithQuery<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
