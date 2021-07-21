package com.github.cloudyrock.mongock.micronaut.base.util.importers;

import java.util.List;

public interface ContextImporter {

  String[] getPaths();

  List<ArtifactDescriptor> getArtifacts();
}
