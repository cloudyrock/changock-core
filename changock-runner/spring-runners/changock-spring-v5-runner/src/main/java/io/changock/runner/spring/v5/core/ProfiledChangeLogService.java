package io.changock.runner.spring.v5.core;

import io.changock.migration.api.AnnotationProcessor;
import io.changock.runner.core.ChangeLogService;
import io.changock.runner.spring.util.ProfileUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

public class ProfiledChangeLogService extends ChangeLogService {

    private static final Function<List<String>, Function<Class, Boolean>> classFilter =
            activeProfiles -> annotated -> ProfileUtil.matchesActiveSpringProfile(activeProfiles, annotated);

    private static final Function<List<String>, Function<Method, Boolean>> methodFilter =
            activeProfiles -> annotated -> ProfileUtil.matchesActiveSpringProfile(activeProfiles, annotated);

    public ProfiledChangeLogService(List<String> changeLogsBasePackageList, String startSystemVersionInclusive, String endSystemVersionInclusive, List<String> activeProfiles, AnnotationProcessor annotationProcessor) {
        super(changeLogsBasePackageList, startSystemVersionInclusive, endSystemVersionInclusive, classFilter.apply(activeProfiles), methodFilter.apply(activeProfiles), annotationProcessor);
    }
}
