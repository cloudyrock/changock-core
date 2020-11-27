package io.changock.runner.spring.v5.config.importers;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MongoSpringDataImporter implements ContextImporter {
  @Override
  public String[] getPaths(Environment environment) {


    try {
      return loadSpringDataContextV3(environment);
    } catch (ClassNotFoundException e) {
      try {
        return loadSpringDataContextV2(environment);
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

  private String[] loadSpringDataContextV2(Environment env) throws ClassNotFoundException {
    String packageName = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.";
    Class.forName(packageName + "SpringDataMongoV2Driver");

    //temporally until deprecated Mongock runner and config is completely removed
    String configImport = isChangockConfig((AbstractEnvironment) env)
        ? "SpringDataMongoV2Configuration"
        : "MongockSpringDataV2Configuration";

    return new String[]{packageName + configImport, packageName + "SpringDataV2Context"};
  }

  private String[] loadSpringDataContextV3(Environment env) throws ClassNotFoundException {
    String packageName = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.";
    Class.forName(packageName + "SpringDataMongoV3Driver");

    //temporally until deprecated Mongock runner and config is completely removed
    String configImport = isChangockConfig((AbstractEnvironment) env)
        ? "SpringDataMongoV3Configuration"
        : "MongockSpringDataV3Configuration";

    return new String[]{packageName + configImport, packageName + "SpringDataV3Context"};
  }

  private boolean isChangockConfig(AbstractEnvironment env) {
    return env.getPropertySources()
        .stream()
        .filter(propertySource -> propertySource instanceof MapPropertySource)
        .map(propertySource -> ((MapPropertySource) propertySource).getSource().keySet())
        .flatMap(Collection::stream)
        .anyMatch(key -> key.toLowerCase().startsWith("changock."));
  }

}
