package com.github.cloudyrock.spring.v5.config.importers;

import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

public class MongoDBSpringDataImporter implements ContextImporter {

  private final static String PACKAGE_TEMPLATE = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v%s.config.";
  private final static String CONTEXT_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sContext";

  @Override
  public String[] getPaths(Environment environment) {
    try {
      return loadSpringDataContextByVersion("2");
    } catch (ClassNotFoundException e) {
      try {
        return loadSpringDataContextByVersion("3");
      } catch (ClassNotFoundException e2) {
        return null;
      }
    }
  }

  @Override
  public List<ArtifactDescriptor> getArtifacts() {
    return Arrays.asList(
        getArtifactDescriptor("2"),
        getArtifactDescriptor("3")
    );
  }

  private ArtifactDescriptor getArtifactDescriptor(String v) {
    return new ArtifactDescriptor("MongoDB Spring data " + v, "com.github.cloudyrock.mongock:mongodb-springdata-v" + v + "-driver");
  }

  private String[] loadSpringDataContextByVersion(String v) throws ClassNotFoundException {
    String contextClassName = String.format(CONTEXT_TEMPLATE, v, v);
    Class.forName(contextClassName);
    return new String[]{
//        MongockSpringConfiguration.class.getCanonicalName(),
        contextClassName};
  }


}
