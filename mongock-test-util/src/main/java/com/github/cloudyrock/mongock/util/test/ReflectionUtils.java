package com.github.cloudyrock.mongock.util.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

// Doing nasty things to avoid change production code
public final class ReflectionUtils {

  private ReflectionUtils() {}

  public static Object getImplementationFromLockGuardProxy(Object proxiedObject) {
    try {
      Object object = java.lang.reflect.Proxy.getInvocationHandler(proxiedObject);
      return ReflectionUtils.getFinalFieldFromObject(object, "implementation");

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static Object getFinalFieldFromObject(Object object, String fieldName) {
    try {
      Field field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
      return field.get(object);
    } catch(Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static boolean isProxy(Object object) {
    try {
      java.lang.reflect.Proxy.getInvocationHandler(object);
      return true;
    } catch (IllegalArgumentException ex) {
      return !"not a proxy instance".equals(ex.getMessage());
    }
  }
}
