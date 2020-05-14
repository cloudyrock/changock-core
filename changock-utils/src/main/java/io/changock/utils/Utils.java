package io.changock.utils;

public final class Utils {

  private Utils() {
  }

  public static boolean isBasicTypeJDK(Class<?> returningType) {
    return returningType.isPrimitive() || String.class.equals(returningType) || Class.class.equals(returningType);
  }
}
