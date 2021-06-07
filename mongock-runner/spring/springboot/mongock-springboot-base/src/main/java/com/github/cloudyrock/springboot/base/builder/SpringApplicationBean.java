package com.github.cloudyrock.springboot.base.builder;

import com.github.cloudyrock.springboot.base.MongockApplicationRunner;
import com.github.cloudyrock.springboot.base.MongockInitializingBeanRunner;

public interface SpringApplicationBean {
  //TODO javadoc
  MongockApplicationRunner buildApplicationRunner();

  //TODO javadoc
  MongockInitializingBeanRunner buildInitializingBeanRunner();
}
