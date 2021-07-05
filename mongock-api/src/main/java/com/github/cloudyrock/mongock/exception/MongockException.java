package com.github.cloudyrock.mongock.exception;

/**
 *
 */
public class MongockException extends RuntimeException {

  public MongockException() {
    super();
  }

  public MongockException(Throwable exception) {
    super(exception);
  }

  public MongockException(String message) {
    super(message);
  }

  public MongockException(String message, Exception e) {
    super(message, e);
  }
}
