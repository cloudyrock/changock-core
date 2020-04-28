package io.changock.driver.mongo.springdata.v2.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.IndexOperationsDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.index.IndexOperations;

public class IndexOperationsDecoratorImpl implements IndexOperationsDecorator {

    private final IndexOperations impl;
    private final MethodInvoker invoker;

    public IndexOperationsDecoratorImpl(IndexOperations impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public IndexOperations getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
