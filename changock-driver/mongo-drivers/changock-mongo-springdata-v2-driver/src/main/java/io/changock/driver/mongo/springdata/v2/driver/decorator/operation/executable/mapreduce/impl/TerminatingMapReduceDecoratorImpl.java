package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.TerminatingMapReduceDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class TerminatingMapReduceDecoratorImpl<T> implements TerminatingMapReduceDecorator<T> {

    private final ExecutableMapReduceOperation.TerminatingMapReduce<T> impl;
    private final MethodInvoker invoker;

    public TerminatingMapReduceDecoratorImpl(ExecutableMapReduceOperation.TerminatingMapReduce<T> implementation, MethodInvoker methodInvoker) {
        this.impl = implementation;
        this.invoker = methodInvoker;
    }
    @Override
    public ExecutableMapReduceOperation.TerminatingMapReduce<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
