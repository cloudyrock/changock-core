package com.github.cloudyrock.mongock.config.executor;

import com.github.cloudyrock.mongock.config.TransactionStrategy;

import java.util.Map;
import java.util.Optional;

public interface ChangeExecutorConfiguration {
  Map<String, Object> getMetadata();

  String getServiceIdentifier();

  boolean isTrackIgnored();

  Optional<Boolean> getTransactionEnabled();
  
  TransactionStrategy getTransactionStrategy();
}
