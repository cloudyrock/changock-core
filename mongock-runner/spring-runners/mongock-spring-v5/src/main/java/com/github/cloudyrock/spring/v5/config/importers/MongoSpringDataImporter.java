package com.github.cloudyrock.spring.v5.config.importers;

import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

public class MongoSpringDataImporter implements ContextImporter {

  private final static String PACKAGE_TEMPLATE = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v%s.";
  private final static String DRIVER_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sDriver";
  private final static String CONTEXT_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sContext";
  private static final String CONFIG_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sConfiguration";

  @Override
  public String[] getPaths(Environment environment) {
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
    String v = "3";
    return Arrays.asList(
        getArtifactDescriptor("3"),
        getArtifactDescriptor("2")
    );
  }

  private ArtifactDescriptor getArtifactDescriptor(String v) {
    return new ArtifactDescriptor("MongoDB Spring data " + v, "com.github.cloudyrock.mongock:mongodb-springdata-v" + v + "-driver");
  }

  private String[] loadSpringDataContextByVersion(String v) throws ClassNotFoundException {
    Class.forName(String.format(DRIVER_TEMPLATE, v, v));
    return new String[]{String.format(CONFIG_TEMPLATE, v, v), String.format(CONTEXT_TEMPLATE, v, v)};
  }


}
