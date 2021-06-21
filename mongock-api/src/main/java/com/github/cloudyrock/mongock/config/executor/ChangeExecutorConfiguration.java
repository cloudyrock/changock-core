package com.github.cloudyrock.mongock.config.executor;

import java.util.Map;
import java.util.Optional;

public interface ChangeExecutorConfiguration {
  Map<String, Object> getMetadata();

  String getServiceIdentifier();

  boolean isTrackIgnored();

  Optional<Boolean> getTransactionEnabled();
}
