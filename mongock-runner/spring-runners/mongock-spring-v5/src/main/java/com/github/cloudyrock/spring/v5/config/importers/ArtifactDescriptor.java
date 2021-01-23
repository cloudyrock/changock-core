package com.github.cloudyrock.spring.v5.config.importers;

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
