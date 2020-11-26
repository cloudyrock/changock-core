package io.changock.runner.spring.v5.config.importers;

import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

public class MongoSpringDataImporter implements ContextImporter {
  @Override
  public String[] getPaths(Environment environment) {

    boolean mongock = environment.containsProperty("mongock");
    boolean changock = environment.containsProperty("changock");

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
    String packageName = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.";
    Class.forName(packageName + "SpringDataMongo2Driver");
    return new String[]{
        packageName + "MongockSpringDataV3Configuration",
        packageName + "MongockSpringDataV2Context"
    };
  }

  private String[] loadSpringDataContextV3() throws ClassNotFoundException {
    String packageName = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.";
    Class.forName(packageName + "SpringDataMongo3Driver");
    return new String[]{
//        packageName + "SpringDataMongoV3Configuration",
        packageName + "MongockSpringDataV3Configuration",
        packageName + "MongockSpringDataV3Context"
    };
  }
}
