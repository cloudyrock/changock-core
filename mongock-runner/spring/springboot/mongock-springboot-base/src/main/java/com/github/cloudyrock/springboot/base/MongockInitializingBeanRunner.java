package com.github.cloudyrock.springboot.base;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import org.springframework.beans.factory.InitializingBean;

import java.util.Optional;

public class MongockInitializingBeanRunner<R> implements InitializingBean {

  private final MongockRunner<R> runner;


  public MongockInitializingBeanRunner(MongockRunner<R> runner) {
    this.runner = runner;
  }

  @Override
  public void afterPropertiesSet() {
    runner.execute();
  }
}
