package com.github.cloudyrock.mongock.micronaut.base.util.importers;

public class ArtifactDescriptor {

  private final String title;
  private final String artifact;

  public ArtifactDescriptor(String title, String artifact) {
    this.title = title;
    this.artifact = artifact;
  }


  public String getTitle() {
    return title;
  }

  public String getArtifact() {
    return artifact;
  }
}
