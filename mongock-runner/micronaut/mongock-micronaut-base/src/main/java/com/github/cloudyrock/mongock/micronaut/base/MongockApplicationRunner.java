package com.github.cloudyrock.mongock.micronaut.base;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import io.micronaut.context.annotation.Context;

import javax.annotation.PostConstruct;

@Context
public class MongockApplicationRunner {

  private final MongockRunner mongockRunner;

  public MongockApplicationRunner(MongockRunner<?> runner) {
    this.mongockRunner = runner;
  }

  @PostConstruct
  public void init() {
    mongockRunner.execute();
  }

}
