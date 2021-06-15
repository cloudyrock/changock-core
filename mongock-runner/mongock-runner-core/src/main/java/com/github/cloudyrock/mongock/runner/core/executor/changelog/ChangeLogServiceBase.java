package com.github.cloudyrock.mongock.runner.core.executor.changelog;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.driver.api.common.Validable;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.mongock.utils.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;


/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public abstract class ChangeLogServiceBase<CHANGELOG extends ChangeLogItem<CHANGESET>, CHANGESET extends ChangeSetItem> implements Validable {

  protected static final Function<Class<?>, Object> DEFAULT_CHANGELOG_INSTANTIATOR = type -> {
    try {
      return type.getConstructor().newInstance();
    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new MongockException(e);
    }
  };


  private final AnnotationProcessor<CHANGESET> annotationProcessor;
  protected Function<AnnotatedElement, Boolean> profileFilter;
  private Function<Class<?>, Object> changeLogInstantiator;
  private List<String> changeLogsBasePackageList = Collections.emptyList();
  private List<Class<?>> changeLogsBaseClassList = Collections.emptyList();
  private ArtifactVersion startSystemVersion = new DefaultArtifactVersion("0");
  private ArtifactVersion endSystemVersion = new DefaultArtifactVersion(String.valueOf(Integer.MAX_VALUE));

  public ChangeLogServiceBase(AnnotationProcessor<CHANGESET> annotationProcessor) {
    this.annotationProcessor = annotationProcessor;
  }

  protected AnnotationProcessor<CHANGESET> getAnnotationProcessor() {
    return annotationProcessor;
  }

  protected List<String> getChangeLogsBasePackageList() {
    return changeLogsBasePackageList;
  }

  public void setChangeLogsBasePackageList(List<String> changeLogsBasePackageList) {
    this.changeLogsBasePackageList = changeLogsBasePackageList;
  }

  protected List<Class<?>> getChangeLogsBaseClassList() {
    return changeLogsBaseClassList;
  }

  public void setChangeLogsBaseClassList(List<Class<?>> changeLogsBaseClassList) {
    this.changeLogsBaseClassList = changeLogsBaseClassList;
  }

  protected ArtifactVersion getStartSystemVersion() {
    return startSystemVersion;
  }

  public void setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = new DefaultArtifactVersion(startSystemVersion);
  }

  protected ArtifactVersion getEndSystemVersion() {
    return endSystemVersion;
  }

  public void setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = new DefaultArtifactVersion(endSystemVersion);
  }

  protected Function<AnnotatedElement, Boolean> getProfileFilter() {
    return profileFilter;
  }

  public void setProfileFilter(Function<AnnotatedElement, Boolean> profileFilter) {
    this.profileFilter = profileFilter;
  }

  protected Optional<Function<Class<?>, Object>> getChangeLogInstantiator() {
    return Optional.ofNullable(changeLogInstantiator);
  }

  public void setChangeLogInstantiator(Function<Class<?>, Object> changeLogInstantiator) {
    this.changeLogInstantiator = changeLogInstantiator;
  }

  @Override
  public void runValidation() throws MongockException {
    if (
        (CollectionUtils.isNullEmpty(changeLogsBasePackageList) || !changeLogsBasePackageList.stream().allMatch(StringUtils::hasText))
            && CollectionUtils.isNullEmpty(changeLogsBaseClassList)) {
      throw new MongockException("Scan package for changeLogs is not set: use appropriate setter");
    }
  }

  public SortedSet<CHANGELOG> fetchChangeLogs() {
    return mergeChangeLogClassesAndPackages()
        .stream()
        .filter(changeLogClass -> this.profileFilter != null ? this.profileFilter.apply(changeLogClass) : true)
        .map(this::buildChangeLogObject)
        .collect(Collectors.toCollection(() -> new TreeSet<>(new ChangeLogComparator(annotationProcessor))));
  }

  private Set<Class<?>> mergeChangeLogClassesAndPackages() {
    //the following check is needed because reflection library will bring the entire classpath in case the changeLogsBasePackageList is empty
    Stream<Class<?>> packageStream = changeLogsBasePackageList == null || changeLogsBasePackageList.isEmpty()
        ? Stream.empty()
        : annotationProcessor.getChangeLogAnnotationClass().stream()
        .map(changeLogClass -> new ArrayList<>(new Reflections(changeLogsBasePackageList).getTypesAnnotatedWith(changeLogClass)))// TODO remove dependency, do own method
        .flatMap(Collection::stream);
    return Stream.concat(packageStream, changeLogsBaseClassList.stream()).collect(Collectors.toSet());
  }

  protected List<CHANGESET> fetchChangeSetMethodsSorted(final Class<?> type) throws MongockException {
    List<CHANGESET> changeSets = getChangeSetWithCompanionMethods(asList(type.getDeclaredMethods()));
    changeSets.sort(new ChangeSetComparator());
    return changeSets;
  }


  private List<CHANGESET> getChangeSetWithCompanionMethods(List<Method> allMethods) throws MongockException {
    Set<String> changeSetIds = new HashSet<>();
    // retrieves all the methods annotated with changeSet, preChangeSets, postChangeSets, etc.
    List<Method> changeSetMethods = allMethods.stream().filter(annotationProcessor::isMethodAnnotatedAsChange).collect(Collectors.toList());
    // retrieves all the rollback methods
    Map<String, Method> rollbackMethods = allMethods
        .stream()
        .filter(annotationProcessor::isRollback)
        .collect(Collectors.toMap(annotationProcessor::getId, method -> method));

    //list to be returned
    List<CHANGESET> result = new ArrayList<>();
    for (final Method changeSetMethod : changeSetMethods) {
      String changeSetId = annotationProcessor.getId(changeSetMethod);
      CHANGESET changeSetItem = annotationProcessor.getChangePerformerItem(changeSetMethod, rollbackMethods.get(changeSetId));

      if (changeSetIds.contains(changeSetId)) {
        throw new MongockException(String.format("Duplicated changeset id found: '%s'", changeSetId));
      }
      changeSetIds.add(changeSetId);
      if (isChangeSetWithinSystemVersionRange(changeSetItem)) {
        result.add(changeSetItem);
      }
    }
    return result;
  }

  //todo Create a SystemVersionChecker
  private boolean isChangeSetWithinSystemVersionRange(CHANGESET changeSetAnn) {
    boolean isWithinVersion = false;
    String versionString = changeSetAnn.getSystemVersion();
    ArtifactVersion version = new DefaultArtifactVersion(versionString);
    if (version.compareTo(startSystemVersion) >= 0 && version.compareTo(endSystemVersion) <= 0) {
      isWithinVersion = true;
    }
    return isWithinVersion;
  }

  protected abstract CHANGELOG buildChangeLogObject(Class<?> type);

  private class ChangeSetComparator implements Comparator<CHANGESET>, Serializable {
    private static final long serialVersionUID = -854690868262484102L;

    @Override
    public int compare(CHANGESET c1, CHANGESET c2) {
      return c1.getOrder().compareTo(c2.getOrder());
    }
  }

  private class ChangeLogComparator implements Comparator<CHANGELOG>, Serializable {
    private static final long serialVersionUID = -358162121872177974L;
    private final AnnotationProcessor<CHANGESET> annotationManager;

    ChangeLogComparator(AnnotationProcessor<CHANGESET> annotationManager) {
      this.annotationManager = annotationManager;
    }


    /**
     * if order1 and order2 are not null and different, it return their compare. If one of then is null, the other is first.
     * If both are null or equals, they are compare bby their names
     */
    @Override
    public int compare(CHANGELOG changeLog1, CHANGELOG changeLog2) {
      String val1 = annotationManager.getChangeLogOrder(changeLog1.getType());
      String val2 = annotationManager.getChangeLogOrder(changeLog2.getType());

      if (StringUtils.hasText(val1) && StringUtils.hasText(val2) && !val1.equals(val2)) {
        return val1.compareTo(val2);
      } else if (StringUtils.hasText(val1) && !StringUtils.hasText(val2)) {
        return -1;
      } else if (StringUtils.hasText(val2) && !StringUtils.hasText(val1)) {
        return 1;
      } else {
        return changeLog1.getType().getCanonicalName().compareTo(changeLog2.getType().getCanonicalName());
      }

    }
  }


}
