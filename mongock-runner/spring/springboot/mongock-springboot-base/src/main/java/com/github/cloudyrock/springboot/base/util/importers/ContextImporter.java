package com.github.cloudyrock.springboot.base.util.importers;

import java.util.List;

public interface ContextImporter {

  String[] getPaths();

  List<ArtifactDescriptor> getArtifacts();
}
