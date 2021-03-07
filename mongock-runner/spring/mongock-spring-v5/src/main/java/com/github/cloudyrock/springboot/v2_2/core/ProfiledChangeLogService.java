package com.github.cloudyrock.springboot.v2_2.core;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.runner.core.executor.ChangeLogService;
import com.github.cloudyrock.spring.util.ProfileUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

public class ProfiledChangeLogService extends ChangeLogService {

    private static final Function<List<String>, Function<Class, Boolean>> classFilter =
            activeProfiles -> annotated -> ProfileUtil.matchesActiveSpringProfile(activeProfiles, annotated);

    private static final Function<List<String>, Function<Method, Boolean>> methodFilter =
            activeProfiles -> annotated -> ProfileUtil.matchesActiveSpringProfile(activeProfiles, annotated);

    public ProfiledChangeLogService(List<String> changeLogsBasePackageList, List<Class<?>> changeLogsBaseClassesList, String startSystemVersionInclusive, String endSystemVersionInclusive, List<String> activeProfiles, AnnotationProcessor annotationProcessor) {
        super(changeLogsBasePackageList, changeLogsBaseClassesList, startSystemVersionInclusive, endSystemVersionInclusive, classFilter.apply(activeProfiles), methodFilter.apply(activeProfiles), annotationProcessor, null);
    }
}
