package io.changock.driver.mongo.springdata.v2.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.ClientSessionDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import com.mongodb.client.ClientSession;

public class ClientSessionDecoratorImpl implements ClientSessionDecorator {

    private final ClientSession impl;
    private final MethodInvoker invoker;

    public ClientSessionDecoratorImpl(ClientSession impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ClientSession getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
