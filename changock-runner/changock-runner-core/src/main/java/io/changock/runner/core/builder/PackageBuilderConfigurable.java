package io.changock.runner.core.builder;

public interface PackageBuilderConfigurable<BUILDER_TYPE extends RunnerBuilderConfigurable> {
  /**
   * Add a changeLog package to be scanned.
   * <b>Mandatory</b>
   * @param changeLogsScanPackage  package to be scanned
   * @return builder for fluent interface
   */
  BUILDER_TYPE addChangeLogsScanPackage(String changeLogsScanPackage);

  BUILDER_TYPE setConfig(ChangockConfiguration config);
}
