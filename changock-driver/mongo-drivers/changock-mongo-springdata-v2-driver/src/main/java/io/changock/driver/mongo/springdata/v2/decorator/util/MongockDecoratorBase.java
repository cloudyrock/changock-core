package io.changock.driver.mongo.springdata.v2.decorator.util;

import io.changock.driver.core.decorator.MethodInvoker;

public interface MongockDecoratorBase<CLASS> {
  CLASS getImpl();

  MethodInvoker getInvoker();
}
