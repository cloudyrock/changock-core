package com.github.cloudyrock.springboot.base;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import org.springframework.beans.factory.InitializingBean;

import java.util.Optional;

public class MongockInitializingBeanRunner<RETURN_TYPE> implements InitializingBean {

  private final MongockRunner<RETURN_TYPE> runner;
  protected Optional<RETURN_TYPE> result;

  public MongockInitializingBeanRunner(MongockRunner<RETURN_TYPE> runner) {
    this.runner = runner;
  }

  public Optional<RETURN_TYPE> getResult() {
    return result;
  }

  @Override
  public void afterPropertiesSet() {
    result = runner.execute();
  }
}
