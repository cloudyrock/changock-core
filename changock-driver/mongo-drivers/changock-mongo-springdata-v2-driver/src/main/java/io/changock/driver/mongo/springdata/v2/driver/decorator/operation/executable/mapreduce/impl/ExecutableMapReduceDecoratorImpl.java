package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.ExecutableMapReduceDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class ExecutableMapReduceDecoratorImpl<T> implements ExecutableMapReduceDecorator<T> {

    private final ExecutableMapReduceOperation.ExecutableMapReduce<T> impl;
    private final LockGuardInvoker invoker;

    public ExecutableMapReduceDecoratorImpl(ExecutableMapReduceOperation.ExecutableMapReduce<T> impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public ExecutableMapReduceOperation.ExecutableMapReduce<T> getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
