package com.github.cloudyrock.springboot.config;

import com.github.cloudyrock.springboot.base.util.importers.MongockDriverContextSelectorUtil;
import com.github.cloudyrock.springboot.config.importers.MongoSpringDataImporter;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Collections;

public class MongockDriverContextSelector implements ImportSelector {

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return MongockDriverContextSelectorUtil.selectImports(Collections.singletonList(
      new MongoSpringDataImporter()
    ));
  }

}
