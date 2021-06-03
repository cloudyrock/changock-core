package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.exception.MongockException;
import java.util.Optional;

public interface MongockRunner<T> {
  
  boolean isExecutionInProgress();

  boolean isEnabled();

  Optional<T> execute() throws MongockException;
}
