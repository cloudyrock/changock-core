package com.github.cloudyrock.mongock.utils;

import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

//TODO move to util module
public final class LogUtils {

  private LogUtils() {
  }

  public static void logMethodWithArguments(Logger logger, String methodName, List<Object> changelogInvocationParameters) {
    String arguments = changelogInvocationParameters.stream()
        .map(obj -> obj != null ? obj.getClass().getName() : "{null argument}")
        .collect(Collectors.joining(", "));
    logger.info("method[{}] with arguments: [{}]", methodName, arguments);

  }
}
