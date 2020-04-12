package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.MapReduceWithMapFunctionDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithMapFunctionDecoratorImpl<T> implements MapReduceWithMapFunctionDecorator<T> {
    private final MethodInvoker invoker;
    private final ExecutableMapReduceOperation.MapReduceWithMapFunction<T> impl;

    public MapReduceWithMapFunctionDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithMapFunction<T> impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ExecutableMapReduceOperation.MapReduceWithMapFunction<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
