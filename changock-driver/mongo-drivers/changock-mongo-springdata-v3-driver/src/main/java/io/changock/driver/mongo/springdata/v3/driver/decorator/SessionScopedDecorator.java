package io.changock.driver.mongo.springdata.v3.driver.decorator;

import io.changock.driver.mongo.springdata.v3.driver.decorator.impl.ClientSessionDecoratorImpl;
import io.changock.driver.mongo.springdata.v3.driver.decorator.impl.SessionCallbackDecoratorImpl;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.mongodb.client.ClientSession;
import org.springframework.data.mongodb.core.SessionCallback;
import org.springframework.data.mongodb.core.SessionScoped;

import java.util.function.Consumer;

public interface SessionScopedDecorator extends SessionScoped {

    SessionScoped getImpl();

    LockGuardInvoker getInvoker();

    @Override
    default  <T> T execute(SessionCallback<T> action, Consumer<ClientSession> doFinally) {
        SessionCallback<T> sessionCallback = new SessionCallbackDecoratorImpl<>(action, getInvoker());
        Consumer<ClientSession> consumer = clientSession -> {
            ClientSession clientSessionDecorator = new ClientSessionDecoratorImpl(clientSession, getInvoker());
            doFinally.accept(clientSessionDecorator);
        };
        return getInvoker().invoke(()-> getImpl().execute(sessionCallback, consumer));
    }
}
