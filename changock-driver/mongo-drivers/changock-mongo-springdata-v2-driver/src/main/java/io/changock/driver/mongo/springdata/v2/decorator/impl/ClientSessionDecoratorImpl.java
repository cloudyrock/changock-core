package io.changock.driver.mongo.springdata.v2.decorator.impl;

import com.mongodb.client.ClientSession;
import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.ClientSessionDecorator;

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
