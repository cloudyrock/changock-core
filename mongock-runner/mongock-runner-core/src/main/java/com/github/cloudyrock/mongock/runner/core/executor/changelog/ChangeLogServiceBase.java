package com.github.cloudyrock.mongock.runner.core.executor.changelog;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.ChangeLogItemBase;
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
public abstract class ChangeLogServiceBase<CHANGELOG extends ChangeLogItemBase> implements Validable {

  protected static final Function<Class<?>, Object> DEFAULT_CHANGELOG_INSTANTIATOR = type -> {
    try {
      return type.getConstructor().newInstance();
    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new MongockException(e);
    }
  };


  private final AnnotationProcessor annotationProcessor;
  protected Function<AnnotatedElement, Boolean> profileFilter;
  private Function<Class<?>, Object> changeLogInstantiator;
  private List<String> changeLogsBasePackageList = Collections.emptyList();
  private List<Class<?>> changeLogsBaseClassList = Collections.emptyList();
  private ArtifactVersion startSystemVersion = new DefaultArtifactVersion("0");
  private ArtifactVersion endSystemVersion = new DefaultArtifactVersion(String.valueOf(Integer.MAX_VALUE));

  public ChangeLogServiceBase(AnnotationProcessor annotationProcessor) {
    this.annotationProcessor = annotationProcessor;
  }

  protected AnnotationProcessor getAnnotationProcessor() {
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

  protected List<Method> fetchChangeSetMethodsSorted(final Class<?> type) throws MongockException {
    final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));
    changeSets.sort(new ChangeSetComparator(annotationProcessor));
    return changeSets;
  }


  private List<Method> filterChangeSetAnnotation(List<Method> allMethods) throws MongockException {
    final Set<String> changeSetIds = new HashSet<>();
    final List<Method> changeSetMethods = new ArrayList<>();
    for (final Method method : allMethods) {
      if (annotationProcessor.isChangeSetAnnotated(method)) {
        ChangeSetItem changeSetItem = annotationProcessor.getChangeSet(method);
        String id = changeSetItem.getId();
        if (changeSetIds.contains(id)) {
          throw new MongockException(String.format("Duplicated changeset id found: '%s'", id));
        }
        changeSetIds.add(id);
        if (isChangeSetWithinSystemVersionRange(changeSetItem)) {
          changeSetMethods.add(method);
        }
      }
    }
    return changeSetMethods;
  }

  //todo Create a SystemVersionChecker
  private boolean isChangeSetWithinSystemVersionRange(ChangeSetItem changeSetAnn) {
    boolean isWithinVersion = false;
    String versionString = changeSetAnn.getSystemVersion();
    ArtifactVersion version = new DefaultArtifactVersion(versionString);
    if (version.compareTo(startSystemVersion) >= 0 && version.compareTo(endSystemVersion) <= 0) {
      isWithinVersion = true;
    }
    return isWithinVersion;
  }

  protected abstract CHANGELOG buildChangeLogObject(Class<?> type);

  private static class ChangeSetComparator implements Comparator<Method>, Serializable {
    private static final long serialVersionUID = -854690868262484102L;
    private final AnnotationProcessor annotationManager;

    ChangeSetComparator(AnnotationProcessor annotationManager) {
      this.annotationManager = annotationManager;
    }

    @Override
    public int compare(Method o1, Method o2) {
      ChangeSetItem c1 = annotationManager.getChangeSet(o1);
      ChangeSetItem c2 = annotationManager.getChangeSet(o2);
      return c1.getOrder().compareTo(c2.getOrder());
    }
  }

  private class ChangeLogComparator implements Comparator<CHANGELOG>, Serializable {
    private static final long serialVersionUID = -358162121872177974L;
    private final AnnotationProcessor annotationManager;

    ChangeLogComparator(AnnotationProcessor annotationManager) {
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
