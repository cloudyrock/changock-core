package com.github.cloudyrock.springboot.v2_2.config.importers;

import org.springframework.core.env.Environment;

import java.util.List;

public interface ContextImporter {

  String[] getPaths(Environment environment);

  List<ArtifactDescriptor> getArtifacts();
}
