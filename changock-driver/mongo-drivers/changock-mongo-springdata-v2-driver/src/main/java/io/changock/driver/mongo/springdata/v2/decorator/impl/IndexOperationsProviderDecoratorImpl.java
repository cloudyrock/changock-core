package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.IndexOperationsProviderDecorator;
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;

public class IndexOperationsProviderDecoratorImpl implements IndexOperationsProviderDecorator {

    private final IndexOperationsProvider impl;
    private final MethodInvoker invoker;

    public IndexOperationsProviderDecoratorImpl(IndexOperationsProvider impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public IndexOperationsProvider getimpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
