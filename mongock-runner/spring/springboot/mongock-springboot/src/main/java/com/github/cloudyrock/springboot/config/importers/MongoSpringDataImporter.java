package com.github.cloudyrock.springboot.config.importers;

import com.github.cloudyrock.springboot.base.util.importers.ArtifactDescriptor;
import com.github.cloudyrock.springboot.base.util.importers.ContextImporter;
import java.util.Arrays;
import java.util.List;

public class MongoSpringDataImporter implements ContextImporter {

  private final static String PACKAGE_TEMPLATE = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v%s.";
  private final static String DRIVER_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sDriver";
  private final static String CONTEXT_TEMPLATE = PACKAGE_TEMPLATE + "config.SpringDataMongoV%sContext";
  private final static String ARTIFACT_TITLE_TEMPLATE = "MongoDB Spring data %s";
  private final static String ARTIFACT_IDENTIFIER_TEMPLATE = "com.github.cloudyrock.mongock:mongodb-springdata-v%s-driver";

  @Override
  public String[] getPaths() {
    try {
      return loadSpringDataContextByVersion("3");
    } catch (ClassNotFoundException e) {
      try {
        return loadSpringDataContextByVersion("2");
      } catch (ClassNotFoundException e2) {
        return null;
      }
    }
  }

  @Override
  public List<ArtifactDescriptor> getArtifacts() {
    return Arrays.asList(
        getArtifactDescriptor("3"),
        getArtifactDescriptor("2")
    );
  }

  private ArtifactDescriptor getArtifactDescriptor(String v) {
    return new ArtifactDescriptor(String.format(ARTIFACT_TITLE_TEMPLATE, v), String.format(ARTIFACT_IDENTIFIER_TEMPLATE, v));
  }

  private String[] loadSpringDataContextByVersion(String v) throws ClassNotFoundException {
    Class.forName(String.format(DRIVER_TEMPLATE, v, v));
    return new String[]{
        String.format(CONTEXT_TEMPLATE, v, v)};
  }


}
