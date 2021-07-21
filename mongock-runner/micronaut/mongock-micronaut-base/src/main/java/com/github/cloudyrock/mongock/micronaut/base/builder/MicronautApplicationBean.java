package com.github.cloudyrock.mongock.micronaut.base.builder;


import com.github.cloudyrock.mongock.micronaut.base.MongockApplicationRunner;
import com.github.cloudyrock.mongock.micronaut.base.MongockInitializingBeanRunner;

public interface MicronautApplicationBean {
  //TODO javadoc
  MongockApplicationRunner buildApplicationRunner();

  //TODO javadoc
  MongockInitializingBeanRunner buildInitializingBeanRunner();
}
