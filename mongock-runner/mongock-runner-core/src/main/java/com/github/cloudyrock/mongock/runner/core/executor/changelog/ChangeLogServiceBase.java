package com.github.cloudyrock.mongock.runner.core.executor.changelog;

import com.github.cloudyrock.mongock.AnnotationProcessor;
import com.github.cloudyrock.mongock.ChangeLogItem;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

//TODO: this can become a Util class, no a service: static methods and name is confusing

/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public abstract class ChangeLogServiceBase<CHANGELOG extends ChangeLogItemBase<?>, CHANGESET extends ChangeSetItem> implements Validable {

  protected static final Function<Class<?>, Object> DEFAULT_CHANGELOG_INSTANTIATOR = type -> {
    try {
      return type.getConstructor().newInstance();
    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new MongockException(e);
    }
  };

  protected final List<String> changeLogsBasePackageList;
  protected final List<Class<?>> changeLogsBaseClassList;
  protected final ArtifactVersion startSystemVersion;
  protected final ArtifactVersion endSystemVersion;
  protected final Function<AnnotatedElement, Boolean> profileFilter;
  protected final AnnotationProcessor<CHANGESET> annotationManager;
  protected final Function<Class<?>, Object> changeLogInstantiator;




  public ChangeLogServiceBase(List<String> changeLogsBasePackageList,
                              List<Class<?>> changeLogsBaseClassList,
                              String startSystemVersionInclusive,
                              String endSystemVersionInclusive,
                              Function<AnnotatedElement, Boolean> profileFilter,
                              AnnotationProcessor<CHANGESET>  annotationProcessor,
                              Function<Class<?>, Object> changeLogInstantiator) {
    this.changeLogsBasePackageList = new ArrayList<>(changeLogsBasePackageList);
    this.changeLogsBaseClassList = changeLogsBaseClassList;
    this.startSystemVersion = new DefaultArtifactVersion(startSystemVersionInclusive);
    this.endSystemVersion = new DefaultArtifactVersion(endSystemVersionInclusive);
    this.profileFilter = profileFilter;
    this.annotationManager = annotationProcessor;
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
        .collect(Collectors.toCollection(() -> new TreeSet<>(new ChangeLogComparator<CHANGELOG>(annotationManager))));
  }

  private Set<Class<?>> mergeChangeLogClassesAndPackages() {
    //the following check is needed because reflection library will bring the entire classpath in case the changeLogsBasePackageList is empty
    Stream<Class<?>> packageStream = changeLogsBasePackageList == null || changeLogsBasePackageList.isEmpty()
        ? Stream.empty()
        : annotationManager.getChangeLogAnnotationClass()
        .stream()
        .map(changeLogClass -> new ArrayList<>(new Reflections(changeLogsBasePackageList).getTypesAnnotatedWith(changeLogClass)))// TODO remove dependency, do own method
        .flatMap(Collection::stream);
    return Stream.concat(packageStream, changeLogsBaseClassList.stream()).collect(Collectors.toSet());
  }

  protected abstract CHANGELOG buildChangeLogObject(Class<?> type);


  protected List<CHANGESET> fetchChangeSetFromClass(Class<?> type) {
    return fetchChangeSetMethodsSorted(type)
        .stream()
        .filter(changeSetMethod -> this.profileFilter != null ? this.profileFilter.apply(changeSetMethod) : true)
        .map(annotationManager::getChangeSet)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private List<Method> fetchChangeSetMethodsSorted(final Class<?> type) throws MongockException {
    final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));
    changeSets.sort(new ChangeSetComparator(annotationManager));
    return changeSets;
  }


  private List<Method> filterChangeSetAnnotation(List<Method> allMethods) throws MongockException {
    final Set<String> changeSetIds = new HashSet<>();
    final List<Method> changeSetMethods = new ArrayList<>();
    for (final Method method : allMethods) {
      if (annotationManager.isChangeSetAnnotated(method)) {
        ChangeSetItem changeSetItem = annotationManager.getChangeSet(method);
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


  private static class ChangeLogComparator<CHANGELOG extends ChangeLogItemBase<?>> implements Comparator<CHANGELOG>, Serializable {
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

}
