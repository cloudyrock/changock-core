package io.changock.driver.mongo.springdata.v3.driver.decorator;

import com.mongodb.client.ClientSession;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.springdata.v3.driver.decorator.impl.ClientSessionDecoratorImpl;
import io.changock.driver.mongo.springdata.v3.driver.decorator.impl.SessionCallbackDecoratorImpl;
import io.changock.migration.api.annotations.DecoratorDiverted;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import org.springframework.data.mongodb.core.SessionCallback;
import org.springframework.data.mongodb.core.SessionScoped;

import java.util.function.Consumer;

public interface SessionScopedDecorator extends SessionScoped {

  SessionScoped getImpl();

  LockGuardInvoker getInvoker();

  @Override
  @DecoratorDiverted
  @NonLockGuarded(NonLockGuardedType.NONE)
  default <T> T execute(SessionCallback<T> sessionCallback, Consumer<ClientSession> clientSessionConsumer) {
    return getImpl().execute(
        new SessionCallbackDecoratorImpl<>(sessionCallback, getInvoker()),
        clientSession -> clientSessionConsumer.accept(new ClientSessionDecoratorImpl(clientSession, getInvoker())));
  }

  @Override
  @DecoratorDiverted
  @NonLockGuarded(NonLockGuardedType.NONE)
  default <T> T execute(SessionCallback<T> sessionCallback) {
    return getImpl().execute(new SessionCallbackDecoratorImpl<>(sessionCallback, getInvoker()), session -> { });
  }
}
