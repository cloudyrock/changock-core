package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.SessionCallbackDecorator;
import org.springframework.data.mongodb.core.SessionCallback;

public class SessionCallbackDecoratorImpl<T> implements SessionCallbackDecorator<T> {

    private final SessionCallback<T> impl;
    private final MethodInvoker invoker;

    public SessionCallbackDecoratorImpl(SessionCallback<T> impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public SessionCallback getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
