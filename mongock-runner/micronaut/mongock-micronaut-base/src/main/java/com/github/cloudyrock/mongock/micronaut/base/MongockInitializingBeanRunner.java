package com.github.cloudyrock.mongock.micronaut.base;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import io.micronaut.context.annotation.Context;
import io.micronaut.runtime.Micronaut;

@Context
public class MongockInitializingBeanRunner{

  public MongockInitializingBeanRunner(MongockRunner<?> runner, Micronaut micronaut) {
  }

}
