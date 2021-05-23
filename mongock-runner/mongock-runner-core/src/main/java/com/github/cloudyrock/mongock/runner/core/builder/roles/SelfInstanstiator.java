package com.github.cloudyrock.mongock.runner.core.builder.roles;

public interface SelfInstanstiator<BUILDER_TYPE> {
  BUILDER_TYPE getInstance();
}
