package com.github.cloudyrock.mongock.runner.core.builder.interfaces;

public interface SelfInstanstiator<BUILDER_TYPE> {
  BUILDER_TYPE getInstance();
}
