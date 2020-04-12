package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.MapReduceWithOptionsDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithOptionsDecoratorImpl<T> implements MapReduceWithOptionsDecorator<T> {

    private final MapReduceWithOptionsDecorator<T> impl;
    private final MethodInvoker invoker;

    public MapReduceWithOptionsDecoratorImpl(MapReduceWithOptionsDecorator<T> impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public ExecutableMapReduceOperation.MapReduceWithOptions<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
