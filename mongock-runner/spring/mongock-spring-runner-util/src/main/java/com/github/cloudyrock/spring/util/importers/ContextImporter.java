package com.github.cloudyrock.spring.util.importers;

import java.util.List;

public interface ContextImporter {

  String[] getPaths();

  List<ArtifactDescriptor> getArtifacts();
}
