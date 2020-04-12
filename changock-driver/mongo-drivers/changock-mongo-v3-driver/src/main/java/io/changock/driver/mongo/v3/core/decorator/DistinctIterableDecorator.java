package io.changock.driver.mongo.v3.core.decorator;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.model.Collation;
import io.changock.driver.mongo.v3.core.decorator.impl.DistinctIterableDecoratorImpl;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface DistinctIterableDecorator<T> extends MongoIterableDecorator<T>, DistinctIterable<T> {

  @Override
  DistinctIterable<T> getImpl();

  @Override
  default DistinctIterable<T> filter(Bson filter) {
    return new DistinctIterableDecoratorImpl<>(getImpl().filter(filter), getInvoker());
  }

  @Override
  default DistinctIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return new DistinctIterableDecoratorImpl<>(getImpl().maxTime(maxTime, timeUnit), getInvoker());
  }

  @Override
  default DistinctIterable<T> batchSize(int batchSize) {
    return new DistinctIterableDecoratorImpl<>(getImpl().batchSize(batchSize).batchSize(batchSize), getInvoker());
  }

  @Override
  default DistinctIterable<T> collation(Collation collation) {
    return new DistinctIterableDecoratorImpl<>(getImpl().collation(collation), getInvoker());
  }
}
