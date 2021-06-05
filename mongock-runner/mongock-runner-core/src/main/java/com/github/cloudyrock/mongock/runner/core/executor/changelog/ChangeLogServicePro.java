package com.github.cloudyrock.mongock.runner.core.executor.changelog;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.pro.ChangeLogItemPro;
import com.github.cloudyrock.mongock.pro.ChangeSetItemPro;
import com.github.cloudyrock.mongock.runner.core.executor.MongockAnnotationProcessorPro;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChangeLogServicePro extends ChangeLogServiceBase<ChangeLogItemPro> {

  private static final MongockAnnotationProcessorPro ANN_PROCESSOR = new MongockAnnotationProcessorPro();


  public ChangeLogServicePro() {
    super(ANN_PROCESSOR);
  }

  @Override
  protected ChangeLogItemPro buildChangeLogObject(Class<?> type) {
    try {
      Function<Class<?>, Object> instantiator = getChangeLogInstantiator().orElse(DEFAULT_CHANGELOG_INSTANTIATOR);
      AnnotationProcessor annProcessor = getAnnotationProcessor();
      return new ChangeLogItemPro(type, instantiator.apply(type), annProcessor.getChangeLogOrder(type), annProcessor.getChangeLogFailFast(type), annProcessor.getChangeLogPreMigration(type), annProcessor.getChangeLogPostMigration(type), fetchChangeSetFromClass(type));
    } catch (MongockException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new MongockException(ex);
    }
  }

  protected List<ChangeSetItemPro> fetchChangeSetFromClass(Class<?> type) {
    return fetchChangeSetMethodsSorted(type)
        .stream()
        .filter(changeSetMethod -> this.profileFilter != null ? this.profileFilter.apply(changeSetMethod) : true)
        .map(ANN_PROCESSOR::getChangeSet)
        .collect(Collectors.toList());
  }


}
