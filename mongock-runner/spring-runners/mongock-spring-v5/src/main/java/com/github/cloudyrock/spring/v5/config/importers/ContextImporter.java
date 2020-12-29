package com.github.cloudyrock.spring.v5.config.importers;

import org.springframework.core.env.Environment;

import java.util.List;

public interface ContextImporter {

  String[] getPaths(Environment environment);

  List<ArtifactDescriptor> getArtifacts();
}
