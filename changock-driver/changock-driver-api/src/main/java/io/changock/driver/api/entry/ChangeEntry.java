package io.changock.driver.api.entry;

import io.changock.driver.api.changelog.ChangeSetItem;

import java.util.Date;

/**
 * Entry in the changes collection log
 * Type: entity class.
 *
 * @since 27/07/2014
 */
public class ChangeEntry {

  private final String executionId;
  private final String changeId;
  private final String author;
  private final Date timestamp;
  private final String changeLogClass;
  private final String changeSetMethodName;
  private final Object metadata;
  private final long executionMillis;


  public static ChangeEntry createInstance(String executionId, ChangeSetItem changeSet, long executionMillis, Object metadata) {
    return new ChangeEntry(
        executionId,
        changeSet.getId(),
        changeSet.getAuthor(),
        new Date(),
        changeSet.getMethod().getDeclaringClass().getName(),
        changeSet.getMethod().getName(),
        executionMillis,
        metadata) {
    };
  }

  public ChangeEntry(String executionId,
                     String changeId,
                     String author,
                     Date timestamp,
                     String changeLogClass,
                     String changeSetMethodName,
                     long executionMillis,
                     Object metadata) {
    this.executionId = executionId;
    this.changeId = changeId;
    this.author = author;
    this.timestamp = new Date(timestamp.getTime());
    this.changeLogClass = changeLogClass;
    this.changeSetMethodName = changeSetMethodName;
    this.executionMillis = executionMillis;
    this.metadata = metadata;
  }


  public String getExecutionId() {
    return executionId;
  }

  public String getChangeId() {
    return this.changeId;
  }

  public String getAuthor() {
    return this.author;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }

  public String getChangeLogClass() {
    return this.changeLogClass;
  }

  public String getChangeSetMethodName() {
    return this.changeSetMethodName;
  }

  public long getExecutionMillis() {
    return executionMillis;
  }

  public Object getMetadata() {
    return metadata;
  }


  @Override
  public String toString() {

    return String.format(
        "Changock change[%s] for method[%s.%s] in execution[%s] at %s for [%d]ms by %s",
        changeId,
        changeLogClass,
        changeSetMethodName,
        executionId,
        timestamp,
        executionMillis,
        author);
  }
}
