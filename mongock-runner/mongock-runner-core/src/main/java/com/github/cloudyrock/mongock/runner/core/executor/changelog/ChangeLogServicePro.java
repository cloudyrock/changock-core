package com.github.cloudyrock.mongock.runner.core.executor.changelog;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.pro.ChangeLogItemPro;
import com.github.cloudyrock.mongock.pro.ChangeSetItemPro;
import com.github.cloudyrock.mongock.runner.core.executor.MongockAnnotationProcessorPro;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public class ChangeLogServicePro extends ChangeLogServiceBase<ChangeLogItemPro, ChangeSetItemPro> {

  /**
   * @param changeLogsBasePackageList   list of changeLog packages
   * @param startSystemVersionInclusive inclusive starting systemVersion
   * @param endSystemVersionInclusive   inclusive ending systemVersion
   */
  public ChangeLogServicePro(List<String> changeLogsBasePackageList, List<Class<?>> changeLogsBaseClassList, String startSystemVersionInclusive, String endSystemVersionInclusive) {
    this(changeLogsBasePackageList, changeLogsBaseClassList, startSystemVersionInclusive, endSystemVersionInclusive, null, new MongockAnnotationProcessorPro(), null);
  }

  /**
   * @param changeLogsBasePackageList   list of changeLog packages
   * @param startSystemVersionInclusive inclusive starting systemVersion
   * @param endSystemVersionInclusive   inclusive ending systemVersion
   * @param annotationProcessor         in case the annotations are different from the ones define in mongock-api, its required a class to manage them
   */
  public ChangeLogServicePro(List<String> changeLogsBasePackageList,
                             List<Class<?>> changeLogsBaseClassList,
                             String startSystemVersionInclusive,
                             String endSystemVersionInclusive,
                             AnnotationProcessor<ChangeSetItemPro> annotationProcessor,
                             Function<Class<?>, Object> changeLogInstantiator) {
    this(changeLogsBasePackageList, changeLogsBaseClassList, startSystemVersionInclusive, endSystemVersionInclusive, null, annotationProcessor, changeLogInstantiator);
  }

  public ChangeLogServicePro(List<String> changeLogsBasePackageList,
                             List<Class<?>> changeLogsBaseClassList,
                             String startSystemVersionInclusive,
                             String endSystemVersionInclusive,
                             Function<AnnotatedElement, Boolean> profileFilter,
                             AnnotationProcessor<ChangeSetItemPro>  annotationProcessor,
                             Function<Class<?>, Object> changeLogInstantiator) {
    super(
        new ArrayList<>(changeLogsBasePackageList),
        changeLogsBaseClassList,
        startSystemVersionInclusive,
        endSystemVersionInclusive,
        profileFilter,
        annotationProcessor != null ? annotationProcessor : new MongockAnnotationProcessorPro(),
        changeLogInstantiator != null ? changeLogInstantiator : DEFAULT_CHANGELOG_INSTANTIATOR);
  }



  @Override
  protected ChangeLogItemPro buildChangeLogObject(Class<?> type) {
    try {
      return new ChangeLogItemPro(type, this.changeLogInstantiator.apply(type), annotationProcessor.getChangeLogOrder(type), annotationProcessor.getChangeLogFailFast(type), annotationProcessor.getChangeLogPreMigration(type), annotationProcessor.getChangeLogPostMigration(type), fetchChangeSetFromClass(type));
    } catch (MongockException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new MongockException(ex);
    }
  }

  @Override
  protected List<ChangeSetItemPro> fetchChangeSetFromClass(Class<?> type) {
    return fetchChangeSetMethodsSorted(type)
        .stream()
        .filter(changeSetMethod -> this.profileFilter != null ? this.profileFilter.apply(changeSetMethod) : true)
        .map(annotationProcessor::getChangeSet)
        .collect(Collectors.toList());
  }

}
