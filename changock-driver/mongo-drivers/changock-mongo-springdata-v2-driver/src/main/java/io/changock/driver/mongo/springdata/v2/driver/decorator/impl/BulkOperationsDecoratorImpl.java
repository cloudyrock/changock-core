package io.changock.driver.mongo.springdata.v2.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.BulkOperationsDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.BulkOperations;

public class BulkOperationsDecoratorImpl implements BulkOperationsDecorator {

    private final BulkOperations impl;
    private final MethodInvoker invoker;

    public BulkOperationsDecoratorImpl(BulkOperations impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public BulkOperations getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
