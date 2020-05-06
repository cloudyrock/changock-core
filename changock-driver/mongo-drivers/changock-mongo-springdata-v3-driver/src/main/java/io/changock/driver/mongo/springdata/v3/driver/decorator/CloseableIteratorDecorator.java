package io.changock.driver.mongo.springdata.v3.driver.decorator;

import io.changock.driver.mongo.syncv4.core.decorator.ChangockIterator;
import org.springframework.data.util.CloseableIterator;

public interface CloseableIteratorDecorator<T> extends CloseableIterator<T>, ChangockIterator<T> {

  CloseableIterator<T> getImpl();

  @Override
  default void close() {
    getInvoker().invoke(() -> getImpl().close());
  }
}
