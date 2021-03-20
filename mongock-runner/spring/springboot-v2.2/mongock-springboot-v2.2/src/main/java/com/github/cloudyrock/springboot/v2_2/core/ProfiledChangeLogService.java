package com.github.cloudyrock.springboot.v2_2.core;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.spring.util.ProfileUtil;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Function;

public class ProfiledChangeLogService extends ChangeLogService {

  private static final Function<List<String>, Function<AnnotatedElement, Boolean>> profileFilter =
      activeProfiles -> annotated -> ProfileUtil.matchesActiveSpringProfile(
          activeProfiles,
          Profile.class,
          annotated,
          (AnnotatedElement element) ->element.getAnnotation(Profile.class).value());



    public ProfiledChangeLogService(List<String> changeLogsBasePackageList, List<Class<?>> changeLogsBaseClassesList, String startSystemVersionInclusive, String endSystemVersionInclusive, List<String> activeProfiles, AnnotationProcessor annotationProcessor) {
        super(changeLogsBasePackageList, changeLogsBaseClassesList, startSystemVersionInclusive, endSystemVersionInclusive, profileFilter.apply(activeProfiles), annotationProcessor, null);
    }
}
