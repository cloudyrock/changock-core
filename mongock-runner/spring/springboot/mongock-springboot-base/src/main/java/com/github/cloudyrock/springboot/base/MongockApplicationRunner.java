package com.github.cloudyrock.springboot.base;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class MongockApplicationRunner<R> implements ApplicationRunner {

  private final MongockRunner<R> runner;

  public MongockApplicationRunner(MongockRunner<R> runner) {
    this.runner = runner;
  }


  @Override
  public void run(ApplicationArguments args) throws Exception {
    runner.execute();
  }
}
