package com.github.cloudyrock.springboot.v2_2.config.importers;

import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

public class MongoSpringDataImporter implements ContextImporter {

  private final static String PACKAGE_TEMPLATE = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v%s.";
  private final static String DRIVER_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sDriver";
  private final static String CONTEXT_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sContext";

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
    return new String[]{
        MongockSpringConfiguration.class.getCanonicalName(),
        String.format(CONTEXT_TEMPLATE, v, v)};
  }


}
