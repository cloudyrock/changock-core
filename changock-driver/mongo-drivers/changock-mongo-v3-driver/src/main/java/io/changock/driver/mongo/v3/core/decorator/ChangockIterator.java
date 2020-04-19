package io.changock.driver.mongo.v3.core.decorator;

import io.changock.driver.core.lock.interceptor.decorator.MethodInvoker;

import java.util.Iterator;

public interface ChangockIterator<T> extends Iterator<T> {

  Iterator<T> getImpl();

  MethodInvoker getInvoker();

  @Override
  default boolean hasNext() {
    return getInvoker().invoke(() -> getImpl().hasNext());
  }

  @Override
  default T next() {
    return getInvoker().invoke(()-> getImpl().next());
  }
}
