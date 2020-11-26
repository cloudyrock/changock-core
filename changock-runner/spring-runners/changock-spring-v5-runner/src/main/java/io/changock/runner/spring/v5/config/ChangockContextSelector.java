package io.changock.runner.spring.v5.config;

import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.spring.v5.config.importers.ContextImporter;
import io.changock.runner.spring.v5.config.importers.MongoSpringDataImporter;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChangockContextSelector implements ImportSelector {


  private final static String DRIVER_NOT_FOUND_ERROR_TEMPLATE = "CHANGOCK DRIVER HAS NOT BEEN IMPORTED" +
      "\n====================================" +
      "\n\tSOLUTION: You need to import one of the following artifacts";

  private static final List<ContextImporter> contextImporters = Collections.singletonList(
      new MongoSpringDataImporter()
  );

  private static final String DRIVER_NOT_FOUND_ERROR;

  static {
    StringBuilder sb = new StringBuilder(DRIVER_NOT_FOUND_ERROR_TEMPLATE);
    contextImporters.stream()
        .map(ContextImporter::getArtifacts)
        .flatMap(List::stream)
        .forEach(desc -> sb.append("\n\t- '").append(desc.getArtifact()).append("' for ").append(desc.getTitle()));
    DRIVER_NOT_FOUND_ERROR =  sb.toString();
  }



  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return contextImporters.stream()
        .map(ContextImporter::getPaths)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() ->  new ChangockException(String.format("\n\n%s\n\n", DRIVER_NOT_FOUND_ERROR)));
  }

}
