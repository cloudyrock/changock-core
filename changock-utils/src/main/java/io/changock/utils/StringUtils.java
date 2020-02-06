package io.changock.utils;

//TODO move to util module
public final class StringUtils {

  private StringUtils() {
  }

  public static boolean hasText(String str) {
    if (str == null || str.isEmpty()) {
      return false;
    }
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }
}
