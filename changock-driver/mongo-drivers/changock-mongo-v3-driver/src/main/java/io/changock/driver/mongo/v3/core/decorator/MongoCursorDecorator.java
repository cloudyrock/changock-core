package io.changock.driver.mongo.v3.core.decorator;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import io.changock.driver.core.decorator.MethodInvoker;

public interface MongoCursorDecorator<T> extends MongoCursor<T> {

  MongoCursor<T> getImpl();

  MethodInvoker getInvoker();

  @Override
  default void close() {
    getInvoker().invoke(() -> getImpl().close());
  }

  @Override
  default boolean hasNext() {
    return getInvoker().invoke(() -> getImpl().hasNext());
  }

  @Override
  default T next() {
    return getInvoker().invoke(() -> getImpl().next());
  }

  @Override
  default T tryNext() {
    return getInvoker().invoke(() -> getImpl().tryNext());
  }

  @Override
  default ServerCursor getServerCursor() {
    return getInvoker().invoke(() -> getImpl().getServerCursor());
  }

  @Override
  default ServerAddress getServerAddress() {
    return getImpl().getServerAddress();
  }
}
