package com.github.cloudyrock.springboot.v2_2.config;

import com.github.cloudyrock.spring.util.importers.MongockDriverContextSelectorUtil;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MongockDriverContextSelector implements ImportSelector {

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return MongockDriverContextSelectorUtil.selectImports();
  }

}
