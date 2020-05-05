package io.changock.driver.mongo.springdata.v3.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v3.driver.decorator.ClientSessionDecorator;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.mongodb.client.ClientSession;

public class ClientSessionDecoratorImpl implements ClientSessionDecorator {

    private final ClientSession impl;
    private final LockGuardInvoker invoker;

    public ClientSessionDecoratorImpl(ClientSession impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ClientSession getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
