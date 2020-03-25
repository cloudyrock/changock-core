package io.changock.driver.api.entry;

import io.changock.migration.api.ChangeSetItem;

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
  private final ChangeState state;
  private final String changeLogClass;
  private final String changeSetMethodName;
  private final Object metadata;
  private final long executionMillis;


  public static ChangeEntry createInstance(String executionId, ChangeState state, ChangeSetItem changeSet, long executionMillis, Object metadata) {
    return new ChangeEntry(
        executionId,
        changeSet.getId(),
        changeSet.getAuthor(),
        new Date(),
        state,
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
                     ChangeState state,
                     String changeLogClass,
                     String changeSetMethodName,
                     long executionMillis,
                     Object metadata) {
    this.executionId = executionId;
    this.changeId = changeId;
    this.author = author;
    this.timestamp = new Date(timestamp.getTime());
    this.state = state;
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

  public ChangeState getState() {
    return state;
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
    return "ChangeEntry{" +
        "executionId='" + executionId + '\'' +
        ", changeId='" + changeId + '\'' +
        ", author='" + author + '\'' +
        ", timestamp=" + timestamp +
        ", state=" + state +
        ", changeLogClass='" + changeLogClass + '\'' +
        ", changeSetMethodName='" + changeSetMethodName + '\'' +
        ", metadata=" + metadata +
        ", executionMillis=" + executionMillis +
        '}';
  }
}
