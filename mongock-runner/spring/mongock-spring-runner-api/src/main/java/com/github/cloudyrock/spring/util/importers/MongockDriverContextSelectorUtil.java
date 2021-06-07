package com.github.cloudyrock.spring.util.importers;

import com.github.cloudyrock.mongock.exception.MongockException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MongockDriverContextSelectorUtil {

  private final static String DRIVER_NOT_FOUND_ERROR_TEMPLATE = "MONGOCK DRIVER HAS NOT BEEN IMPORTED" +
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
    DRIVER_NOT_FOUND_ERROR = sb.toString();
  }


  public static String[] selectImports() {
    return contextImporters.stream()
        .map(contextImporter -> contextImporter.getPaths())
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new MongockException(String.format("\n\n%s\n\n", DRIVER_NOT_FOUND_ERROR)));
  }

}
