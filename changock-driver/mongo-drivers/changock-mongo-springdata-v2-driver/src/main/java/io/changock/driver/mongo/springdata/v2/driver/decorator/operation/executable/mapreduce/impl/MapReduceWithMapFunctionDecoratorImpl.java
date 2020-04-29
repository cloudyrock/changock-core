package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.MapReduceWithMapFunctionDecorator;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithMapFunctionDecoratorImpl<T> implements MapReduceWithMapFunctionDecorator<T> {
    private final LockGuardInvoker invoker;
    private final ExecutableMapReduceOperation.MapReduceWithMapFunction<T> impl;

    public MapReduceWithMapFunctionDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithMapFunction<T> impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ExecutableMapReduceOperation.MapReduceWithMapFunction<T> getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
