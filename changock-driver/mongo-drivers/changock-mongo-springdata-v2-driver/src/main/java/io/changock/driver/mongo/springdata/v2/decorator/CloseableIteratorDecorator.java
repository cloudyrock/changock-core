package io.changock.driver.mongo.springdata.v2.decorator;

import io.changock.driver.mongo.v3.core.decorator.ChangockIterator;
import org.springframework.data.util.CloseableIterator;

public interface CloseableIteratorDecorator<T> extends CloseableIterator<T>, ChangockIterator<T> {

  CloseableIterator<T> getImpl();

  @Override
  default void close() {
    getInvoker().invoke(() -> getImpl().close());
  }
}
