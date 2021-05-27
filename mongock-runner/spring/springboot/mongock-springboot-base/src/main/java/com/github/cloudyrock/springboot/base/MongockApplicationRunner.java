package com.github.cloudyrock.springboot.base;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Optional;

public class MongockApplicationRunner<R> implements ApplicationRunner {

  private final MongockRunner<R> runner;
  protected Optional<R> result;

  public MongockApplicationRunner(MongockRunner<R> runner) {
    this.runner = runner;
  }

  public Optional<R> getResult() {
    return result;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    result = runner.execute();
  }
}
