package com.github.cloudyrock.springboot.base;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Optional;

public class MongockApplicationRunner<RETURN_TYPE> implements ApplicationRunner {

  private final MongockRunner<RETURN_TYPE> runner;
  protected Optional<RETURN_TYPE> result;

  public MongockApplicationRunner(MongockRunner<RETURN_TYPE> runner) {
    this.runner = runner;
  }

  public Optional<RETURN_TYPE> getResult() {
    return result;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    result = runner.execute();
  }
}
