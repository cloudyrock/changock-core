package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.ScriptOperationsDecorator;
import org.springframework.data.mongodb.core.ScriptOperations;

@Deprecated
public class ScriptOperationsDecoratorImpl implements ScriptOperationsDecorator {

    private final ScriptOperations impl;
    private final MethodInvoker invoker;

    public ScriptOperationsDecoratorImpl(ScriptOperations impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ScriptOperations getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
