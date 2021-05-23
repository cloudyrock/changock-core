package com.github.cloudyrock.mongock.runner.core.builder.roles;

public interface ServiceIdentificable<BUILDER_TYPE extends ServiceIdentificable> {
  /**
   * Set up the name of the service running mongock.
   * This will be used as a suffix to the hostname when saving changelogs history in database.
   * <b>Optional</b> Default value null
   *
   * @param serviceIdentifier Identifier of the service running mongock
   * @return builder for fluent interface
   */
  BUILDER_TYPE setServiceIdentifier(String serviceIdentifier);
}
