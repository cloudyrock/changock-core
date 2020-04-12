package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.ExecutableMapReduceOperationDecorator;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class ExecutableMapReduceOperationDecoratorImpl implements ExecutableMapReduceOperationDecorator {

    private final ExecutableMapReduceOperation impl;
    private final MethodInvoker invoker;

    public ExecutableMapReduceOperationDecoratorImpl(ExecutableMapReduceOperation impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public ExecutableMapReduceOperation getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
