package io.changock.migration.api.exception;

/**
 *
 */
public class ChangockException extends RuntimeException {
  public ChangockException(Exception exception) {
    super(exception);
  }

  public ChangockException(String message) {
    super(message);
  }

  public ChangockException(String message, Exception e) {
    super(message, e);
  }
}
