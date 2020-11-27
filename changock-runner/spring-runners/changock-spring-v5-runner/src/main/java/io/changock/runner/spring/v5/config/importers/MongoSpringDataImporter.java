package io.changock.runner.spring.v5.config.importers;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MongoSpringDataImporter implements ContextImporter {

  private final static String PACKAGE_TEMPLATE = "com.github.cloudyrock.mongock.driver.mongodb.springdata.v%s.";
  private final static String DRIVER_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sDriver";
  private final static String CONTEXT_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sContext";
  private static final String CONFIG_TEMPLATE = PACKAGE_TEMPLATE + "SpringDataMongoV%sConfiguration";
  private static final String DEPRECATED_CONFIG_TEMPLATE = PACKAGE_TEMPLATE + "MongockSpringDataV%sConfiguration";

  @Override
  public String[] getPaths(Environment environment) {
    try {
      return loadSpringDataContextByVersion((AbstractEnvironment) environment, "3");
    } catch (ClassNotFoundException e) {
      try {
        return loadSpringDataContextByVersion((AbstractEnvironment) environment, "2");
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


  private String[] loadSpringDataContextByVersion(AbstractEnvironment env, String v) throws ClassNotFoundException {
    Class.forName(String.format(DRIVER_TEMPLATE, v, v));

    //temporally until deprecated Mongock runner and config is completely removed
    String configImport = String.format(isChangockConfig(env) ? CONFIG_TEMPLATE : DEPRECATED_CONFIG_TEMPLATE, v, v);

    return new String[]{configImport, String.format(CONTEXT_TEMPLATE, v, v)};
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
