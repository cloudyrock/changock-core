package com.github.cloudyrock.springboot.config;

import com.github.cloudyrock.springboot.base.util.importers.MongockDriverContextSelectorUtil;
import com.github.cloudyrock.springboot.config.importers.MongoSpringDataImporter;
import java.util.Collections;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MongockDriverContextSelector implements ImportSelector {

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return MongockDriverContextSelectorUtil.selectImports(Collections.singletonList(
      new MongoSpringDataImporter()
    ));
  }

}
