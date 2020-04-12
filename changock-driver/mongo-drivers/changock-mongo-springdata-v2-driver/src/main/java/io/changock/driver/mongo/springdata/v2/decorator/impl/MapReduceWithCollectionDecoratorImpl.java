package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.MapReduceWithCollectionDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithCollectionDecoratorImpl<T> implements MapReduceWithCollectionDecorator<T> {

    private final ExecutableMapReduceOperation.MapReduceWithCollection<T> impl;
    private final MethodInvoker invoker;

    public MapReduceWithCollectionDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithCollection<T> impl,
                                                MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ExecutableMapReduceOperation.MapReduceWithCollection<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
