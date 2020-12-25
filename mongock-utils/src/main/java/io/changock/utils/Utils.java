package io.changock.utils;

public final class Utils {

  private Utils() {
  }

  public static boolean isBasicTypeJDK(Class<?> clazz) {
    return clazz.isPrimitive()
        || String.class.equals(clazz)
        || Class.class.equals(clazz)
        || isWrapper(clazz);
  }

  private static boolean isWrapper(Class<?> clazz) {
    return Boolean.class.equals(clazz)
        || Character.class.equals(clazz)
        || Byte.class.equals(clazz)
        || Short.class.equals(clazz)
        || Integer.class.equals(clazz)
        || Long.class.equals(clazz)
        || Float.class.equals(clazz)
        || Double.class.equals(clazz)
        || Void.class.equals(clazz);
  }
}
