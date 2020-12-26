package com.github.cloudyrock.mongock.exception;

/**
 *
 */
public class MongockException extends RuntimeException {
  public MongockException(Exception exception) {
    super(exception);
  }

  public MongockException(String message) {
    super(message);
  }

  public MongockException(String message, Exception e) {
    super(message, e);
  }
}
