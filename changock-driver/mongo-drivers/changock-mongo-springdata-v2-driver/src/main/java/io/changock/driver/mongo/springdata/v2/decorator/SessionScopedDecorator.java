package io.changock.driver.mongo.springdata.v2.decorator;

import com.mongodb.client.ClientSession;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.impl.ClientSessionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.decorator.impl.SessionCallbackDecoratorImpl;
import org.springframework.data.mongodb.core.SessionCallback;
import org.springframework.data.mongodb.core.SessionScoped;

import java.util.function.Consumer;

public interface SessionScopedDecorator extends SessionScoped {

    SessionScoped getImpl();

    MethodInvoker getInvoker();

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
