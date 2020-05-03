package io.changock.driver.mongo.v3.core.decorator;

import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;

import java.util.Iterator;

public interface ChangockIterator<T> extends Iterator<T> {

  Iterator<T> getImpl();

  LockGuardInvoker getInvoker();

  @Override
  default boolean hasNext() {
    return getInvoker().invoke(() -> getImpl().hasNext());
  }

  @Override
  default T next() {
    return getInvoker().invoke(()-> getImpl().next());
  }

  @Override
  default void remove() {
    throw new UnsupportedOperationException("remove");
  }
}
