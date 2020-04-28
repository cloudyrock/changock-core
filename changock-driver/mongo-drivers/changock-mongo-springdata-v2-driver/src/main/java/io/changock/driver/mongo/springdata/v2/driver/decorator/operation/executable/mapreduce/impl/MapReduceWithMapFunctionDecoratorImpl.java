package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.MapReduceWithMapFunctionDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
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
