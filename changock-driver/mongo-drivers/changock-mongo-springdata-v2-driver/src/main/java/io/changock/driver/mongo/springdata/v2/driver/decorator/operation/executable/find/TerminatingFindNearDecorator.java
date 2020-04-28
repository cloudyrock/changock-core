package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find;

import io.changock.driver.mongo.springdata.v2.driver.decorator.util.Invokable;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface TerminatingFindNearDecorator<T> extends Invokable, ExecutableFindOperation.TerminatingFindNear<T> {

  ExecutableFindOperation.TerminatingFindNear<T> getImpl();

  @Override
  default GeoResults<T> all() {
    return getInvoker().invoke(()-> getImpl().all());
  }
}
