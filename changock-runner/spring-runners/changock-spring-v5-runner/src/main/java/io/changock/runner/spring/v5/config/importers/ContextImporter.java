package io.changock.runner.spring.v5.config.importers;

import java.util.List;

public interface ContextImporter {

  String[] getPaths();

  List<ArtifactDescriptor> getArtifacts();
}
