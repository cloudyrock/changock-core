package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.TerminatingMapReduceDecorator;
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
