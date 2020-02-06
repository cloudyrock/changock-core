package io.changock.driver.base.lock;

import io.changock.migration.api.exception.ChangockException;

/**
 *
 * @since 04/04/2018
 */
public class LockPersistenceException extends RuntimeException {

  private static final long serialVersionUID = -4232386506613422980L;

  public LockPersistenceException(String msg) {
    super(msg);
  }

  public LockPersistenceException(ChangockException ex) {
    super(ex);
  }
}
