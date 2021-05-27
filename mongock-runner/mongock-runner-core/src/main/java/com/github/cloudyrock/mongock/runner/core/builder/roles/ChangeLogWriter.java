package com.github.cloudyrock.mongock.runner.core.builder.roles;

import java.util.Map;

public interface ChangeLogWriter<SELF extends ChangeLogWriter<SELF>> {
  /**
   * Indicates if the ignored changeSets should be tracked or not
   *
   * @param trackIgnored if the ignored changeSets should be tracked
   * @return builder for fluent interface
   */
  SELF setTrackIgnored(boolean trackIgnored);


  /**
   * Set the metadata for the Mongock process. This metadata will be added to each document in the MongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   * <b>Optional</b> Default value empty Map
   *
   * @param metadata Custom metadata object  to be added
   * @return builder for fluent interface
   */
  SELF withMetadata(Map<String, Object> metadata);
}
