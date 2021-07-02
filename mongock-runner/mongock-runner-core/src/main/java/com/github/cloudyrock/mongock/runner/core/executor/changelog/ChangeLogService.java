package com.github.cloudyrock.mongock.runner.core.executor.changelog;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import com.github.cloudyrock.mongock.exception.MongockException;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public class ChangeLogService extends ChangeLogServiceBase<ChangeLogItem<ChangeSetItem>, ChangeSetItem> {


  private static final MongockAnnotationProcessor DEFAULT_ANNOTATION_PROCESSOR = new MongockAnnotationProcessor();

  /**
   * @param changeLogsBasePackageList   list of changeLog packages
   * @param startSystemVersionInclusive inclusive starting systemVersion
   * @param endSystemVersionInclusive   inclusive ending systemVersion
   */
  public ChangeLogService(List<String> changeLogsBasePackageList, List<Class<?>> changeLogsBaseClassList, String startSystemVersionInclusive, String endSystemVersionInclusive) {
    this(changeLogsBasePackageList, changeLogsBaseClassList, startSystemVersionInclusive, endSystemVersionInclusive, null, new MongockAnnotationProcessor(), null);
  }

  /**
   * @param changeLogsBasePackageList   list of changeLog packages
   * @param startSystemVersionInclusive inclusive starting systemVersion
   * @param endSystemVersionInclusive   inclusive ending systemVersion
   * @param annotationProcessor         in case the annotations are different from the ones define in mongock-api, its required a class to manage them
   */
  public ChangeLogService(List<String> changeLogsBasePackageList,
                          List<Class<?>> changeLogsBaseClassList,
                          String startSystemVersionInclusive,
                          String endSystemVersionInclusive,
                          AnnotationProcessor<ChangeSetItem> annotationProcessor,
                          Function<Class<?>, Object> changeLogInstantiator) {
    this(changeLogsBasePackageList, changeLogsBaseClassList, startSystemVersionInclusive, endSystemVersionInclusive, null, annotationProcessor, changeLogInstantiator);
  }

  public ChangeLogService(List<String> changeLogsBasePackageList,
                          List<Class<?>> changeLogsBaseClassList,
                          String startSystemVersionInclusive,
                          String endSystemVersionInclusive,
                          Function<AnnotatedElement, Boolean> profileFilter,
                          AnnotationProcessor<ChangeSetItem> annotationProcessor,
                          Function<Class<?>, Object> changeLogInstantiator) {
    this();
    setChangeLogsBasePackageList(new ArrayList<>(changeLogsBasePackageList));
    setChangeLogsBaseClassList(changeLogsBaseClassList);
    setStartSystemVersion(startSystemVersionInclusive);
    setEndSystemVersion(endSystemVersionInclusive);
    setProfileFilter(profileFilter);
    setChangeLogInstantiator(changeLogInstantiator != null ? changeLogInstantiator : DEFAULT_CHANGELOG_INSTANTIATOR);
  }

  public ChangeLogService() {
    super(DEFAULT_ANNOTATION_PROCESSOR);
  }


  @Override
  protected ChangeLogItem<ChangeSetItem> buildChangeLogObject(Class<?> changeLogClass, Function<Class<?>, Object> instantiator, AnnotationProcessor<ChangeSetItem> annProcessor) {
      return new ChangeLogItem<>(changeLogClass,
                                instantiator.apply(changeLogClass),
                                annProcessor.getChangeLogOrder(changeLogClass),
                                annProcessor.isFailFast(changeLogClass),
                                fetchListOfChangeSetsFromClass(changeLogClass),
                                fetchListOfBeforeChangeSetsFromClass(changeLogClass),
                                fetchListOfAfterChangeSetsFromClass(changeLogClass));
  }

}
