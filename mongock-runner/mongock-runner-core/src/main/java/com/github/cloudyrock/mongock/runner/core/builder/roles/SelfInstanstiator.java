package com.github.cloudyrock.mongock.runner.core.builder.roles;

public interface SelfInstanstiator<SELF extends SelfInstanstiator<SELF>> {
  SELF getInstance();
}
