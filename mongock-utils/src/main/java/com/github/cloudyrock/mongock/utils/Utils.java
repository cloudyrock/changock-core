package com.github.cloudyrock.mongock.utils;

import java.net.ContentHandlerFactory;

public final class Utils {

  private Utils() {
  }

  public static boolean isBasicTypeJDK(Class<?> clazz) {
    return clazz.isPrimitive()
        || String.class.equals(clazz)
        || Class.class.equals(clazz)
        || isJDKWrapper(clazz)
        || isJDKCollection(clazz)
        || isOtherJDKClassNonProxiable(clazz);


  }

  private static boolean isOtherJDKClassNonProxiable(Class<?> clazz) {
    return ContentHandlerFactory.class.isAssignableFrom(clazz);
  }

  private static boolean isJDKWrapper(Class<?> clazz) {
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

  private static boolean isJDKCollection(Class<?> clazz) {
    return Iterable.class.isAssignableFrom(clazz);
  }
}
