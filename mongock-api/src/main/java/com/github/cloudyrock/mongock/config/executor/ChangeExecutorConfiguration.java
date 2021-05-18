package com.github.cloudyrock.mongock.config.executor;

import java.util.Map;

public interface ChangeExecutorConfiguration {
  Map<String, Object> getMetadata();
  String getServiceIdentifier();
  boolean isTrackIgnored();
}
