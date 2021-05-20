package com.github.cloudyrock.mongock.runner.core.builder.interfaces;

import java.util.Map;

public interface ChangeLogWriter<BUILDER_TYPE extends ChangeLogWriter> {
  /**
   * Indicates if the ignored changeSets should be tracked or not
   *
   * @param trackIgnored if the ignored changeSets should be tracked
   * @return builder for fluent interface
   */
  BUILDER_TYPE setTrackIgnored(boolean trackIgnored);


  /**
   * Set the metadata for the Mongock process. This metadata will be added to each document in the MongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   * <b>Optional</b> Default value empty Map
   *
   * @param metadata Custom metadata object  to be added
   * @return builder for fluent interface
   */
  BUILDER_TYPE withMetadata(Map<String, Object> metadata);
}
