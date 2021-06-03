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
import java.util.stream.Collectors;

//TODO: this can become a Util class, no a service: static methods and name is confusing

/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public class ChangeLogService extends ChangeLogServiceBase<ChangeLogItem, ChangeSetItem> {

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
                              AnnotationProcessor<ChangeSetItem>  annotationProcessor,
                              Function<Class<?>, Object> changeLogInstantiator) {
    super(
        new ArrayList<>(changeLogsBasePackageList),
        changeLogsBaseClassList,
        startSystemVersionInclusive,
        endSystemVersionInclusive,
        profileFilter,
        annotationProcessor != null ? annotationProcessor : new MongockAnnotationProcessor(),
        changeLogInstantiator != null ? changeLogInstantiator : DEFAULT_CHANGELOG_INSTANTIATOR);
  }



  @Override
  protected ChangeLogItem buildChangeLogObject(Class<?> type) {
    try {
      return new ChangeLogItem(type, this.changeLogInstantiator.apply(type), annotationProcessor.getChangeLogOrder(type), annotationProcessor.getChangeLogFailFast(type), annotationProcessor.getChangeLogPreMigration(type), annotationProcessor.getChangeLogPostMigration(type), fetchChangeSetFromClass(type));
    } catch (MongockException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new MongockException(ex);
    }
  }

  @Override
  protected List<ChangeSetItem> fetchChangeSetFromClass(Class<?> type) {
    return fetchChangeSetMethodsSorted(type)
        .stream()
        .filter(changeSetMethod -> this.profileFilter != null ? this.profileFilter.apply(changeSetMethod) : true)
        .map(annotationProcessor::getChangeSet)
        .collect(Collectors.toList());
  }

}
