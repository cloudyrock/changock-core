package io.changock.runner.spring.v5.config.importers;

import java.util.Arrays;
import java.util.List;

public class MongoSpringDataImporter implements ContextImporter {
  @Override
  public String[] getPaths() {
    try {
      return loadSpringDataContextV3();
    } catch (ClassNotFoundException e) {
      try {
        return loadSpringDataContextV2();
      } catch (ClassNotFoundException e2) {
        return null;
      }
    }
  }

  @Override
  public List<ArtifactDescriptor> getArtifacts() {
    return Arrays.asList(
        new ArtifactDescriptor("MongoDB Spring data 3", "com.github.cloudyrock.mongock:mongodb-springdata-v3-driver"),
        new ArtifactDescriptor("MongoDB Spring data 2", "com.github.cloudyrock.mongock:mongodb-springdata-v2-driver")
    );
  }

  private String[] loadSpringDataContextV2() throws ClassNotFoundException {
    Class.forName("com.github.cloudyrock.config.driver.mongodb.springdata.v2.SpringDataMongo2Driver");
    return new String[]{
        "com.github.cloudyrock.config.driver.mongodb.springdata.v2.MongockSpringDataV3Configuration",
        "com.github.cloudyrock.config.driver.mongodb.springdata.v2.MongockSpringDataV2Context"
    };
  }

  private String[] loadSpringDataContextV3() throws ClassNotFoundException {
    Class.forName("com.github.cloudyrock.config.driver.mongodb.springdata.v3.SpringDataMongo3Driver");
    return new String[]{
        "com.github.cloudyrock.config.driver.mongodb.springdata.v3.MongockSpringDataV3Configuration",
        "com.github.cloudyrock.config.driver.mongodb.springdata.v3.MongockSpringDataV3Context"
    };
  }
}
