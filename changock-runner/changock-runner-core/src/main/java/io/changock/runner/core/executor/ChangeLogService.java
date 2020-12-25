package io.changock.runner.core.executor;

import io.changock.driver.api.common.Validable;
import io.changock.migration.api.AnnotationProcessor;
import io.changock.migration.api.ChangeLogItem;
import io.changock.migration.api.ChangeSetItem;
import io.changock.migration.api.ChangockAnnotationProcessor;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.CollectionUtils;
import io.changock.utils.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;

import java.io.Serializable;
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
public class ChangeLogService implements Validable {

  private final List<String> changeLogsBasePackageList;
  private final List<Class<?>> changeLogsBaseClassList;
  private final ArtifactVersion startSystemVersion;
  private final ArtifactVersion endSystemVersion;
  private final Function<Class, Boolean> changeLogFilter;
  private final Function<Method, Boolean> changeSetFilter;
  private final AnnotationProcessor annotationManager;

  /**
   * @param changeLogsBasePackageList   list of changeLog packages
   * @param startSystemVersionInclusive inclusive starting systemVersion
   * @param endSystemVersionInclusive   inclusive ending systemVersion
   */
  public ChangeLogService(List<String> changeLogsBasePackageList, List<Class<?>> changeLogsBaseClassList, String startSystemVersionInclusive, String endSystemVersionInclusive) {
    this(changeLogsBasePackageList, changeLogsBaseClassList, startSystemVersionInclusive, endSystemVersionInclusive, null, null, new ChangockAnnotationProcessor());
  }

  /**
   * @param changeLogsBasePackageList   list of changeLog packages
   * @param startSystemVersionInclusive inclusive starting systemVersion
   * @param endSystemVersionInclusive   inclusive ending systemVersion
   * @param annotationProcessor         in case the annotations are different from the ones define in mongock-api, its required a class to manage them
   */
  public ChangeLogService(List<String> changeLogsBasePackageList, List<Class<?>> changeLogsBaseClassList, String startSystemVersionInclusive, String endSystemVersionInclusive, AnnotationProcessor annotationProcessor) {
    this(changeLogsBasePackageList, changeLogsBaseClassList, startSystemVersionInclusive, endSystemVersionInclusive, null, null, annotationProcessor);
  }

  protected ChangeLogService(List<String> changeLogsBasePackageList,
                             List<Class<?>> changeLogsBaseClassList,
                             String startSystemVersionInclusive,
                             String endSystemVersionInclusive,
                             Function<Class, Boolean> changeLogFilter,
                             Function<Method, Boolean> changeSetFilter,
                             AnnotationProcessor annotationProcessor) {
    this.changeLogsBasePackageList = new ArrayList<>(changeLogsBasePackageList);
    this.changeLogsBaseClassList = changeLogsBaseClassList;
    this.startSystemVersion = new DefaultArtifactVersion(startSystemVersionInclusive);
    this.endSystemVersion = new DefaultArtifactVersion(endSystemVersionInclusive);
    this.changeLogFilter = changeLogFilter;
    this.changeSetFilter = changeSetFilter;
    this.annotationManager = annotationProcessor != null ? annotationProcessor : new ChangockAnnotationProcessor();
  }

  @Override
  public void runValidation() throws ChangockException {
    if (
        (CollectionUtils.isNullEmpty(changeLogsBasePackageList) || !changeLogsBasePackageList.stream().allMatch(StringUtils::hasText))
            && CollectionUtils.isNullEmpty(changeLogsBaseClassList)) {
      throw new ChangockException("Scan package for changeLogs is not set: use appropriate setter");
    }
  }

  public SortedSet<ChangeLogItem> fetchChangeLogs() {
    return mergeChangeLogClassesAndPackages()
        .stream()
        .filter(changeLogClass -> this.changeLogFilter != null ? this.changeLogFilter.apply(changeLogClass) : true)
        .map(this::buildChangeLogObject)
        .collect(Collectors.toCollection(() -> new TreeSet<>(new ChangeLogComparator(annotationManager))));
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

  private ChangeLogItem buildChangeLogObject(Class<?> type) {
    try {
      return new ChangeLogItem(type, type.getConstructor().newInstance(), annotationManager.getChangeLogOrder(type), fetchChangeSetFromClass(type));
    } catch (ChangockException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new ChangockException(ex);
    }
  }

  private List<ChangeSetItem> fetchChangeSetFromClass(Class<?> type) {
    return fetchChangeSetMethodsSorted(type)
        .stream()
        .filter(changeSetMethod -> this.changeSetFilter != null ? this.changeSetFilter.apply(changeSetMethod) : true)
        .map(annotationManager::getChangeSet)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private List<Method> fetchChangeSetMethodsSorted(final Class<?> type) throws ChangockException {
    final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));
    changeSets.sort(new ChangeSetComparator(annotationManager));
    return changeSets;
  }


  private List<Method> filterChangeSetAnnotation(List<Method> allMethods) throws ChangockException {
    final Set<String> changeSetIds = new HashSet<>();
    final List<Method> changeSetMethods = new ArrayList<>();
    for (final Method method : allMethods) {
      if (annotationManager.isChangeSetAnnotated(method)) {
        ChangeSetItem changeSetItem = annotationManager.getChangeSet(method);
        String id = changeSetItem.getId();
        if (changeSetIds.contains(id)) {
          throw new ChangockException(String.format("Duplicated changeset id found: '%s'", id));
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


  private static class ChangeLogComparator implements Comparator<ChangeLogItem>, Serializable {
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
    public int compare(ChangeLogItem changeLog1, ChangeLogItem changeLog2) {
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
