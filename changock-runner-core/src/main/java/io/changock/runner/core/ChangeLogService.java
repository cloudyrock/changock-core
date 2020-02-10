package io.changock.runner.core;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import io.changock.driver.api.common.Validable;
import io.changock.driver.api.changelog.ChangeLogItem;
import io.changock.driver.api.changelog.ChangeSetItem;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.CollectionUtils;
import io.changock.utils.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

//TODO: this can become a Util class, no a service: static methods and name is confusing

/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public class ChangeLogService implements Validable {

  private final List<String> changeLogsBasePackageList;
  private final ArtifactVersion startSystemVersion;
  private final ArtifactVersion endSystemVersion;
  private final Function<Class, Boolean> changeLogFilter;
  private final Function<Method, Boolean> changeSetFilter;

  /**
   * @param changeLogsBasePackageList   list of changeLog packages
   * @param startSystemVersionInclusive inclusive starting systemVersion
   * @param endSystemVersionInclusive   inclusive ending systemVersion
   */
  public ChangeLogService(List<String> changeLogsBasePackageList, String startSystemVersionInclusive, String endSystemVersionInclusive) {
    this(changeLogsBasePackageList, startSystemVersionInclusive, endSystemVersionInclusive, null, null);
  }

  protected ChangeLogService(List<String> changeLogsBasePackageList,
                             String startSystemVersionInclusive,
                             String endSystemVersionInclusive,
                             Function<Class, Boolean> changeLogFilter,
                             Function<Method, Boolean> changeSetFilter) {
    this.changeLogsBasePackageList = new ArrayList<>(changeLogsBasePackageList);
    this.startSystemVersion = new DefaultArtifactVersion(startSystemVersionInclusive);
    this.endSystemVersion = new DefaultArtifactVersion(endSystemVersionInclusive);
    this.changeLogFilter = changeLogFilter;
    this.changeSetFilter = changeSetFilter;
  }


  @Override
  public void runValidation() throws ChangockException {
    if (CollectionUtils.isNullEmpty(changeLogsBasePackageList) || !changeLogsBasePackageList.stream().allMatch(StringUtils::hasText)) {
      throw new ChangockException("Scan package for changeLogs is not set: use appropriate setter");
    }
  }

  public List<ChangeLogItem> fetchChangeLogs() {
    return fetchChangeLogClassesSorted()
        .stream()
        .filter(changeLogClass -> this.changeLogFilter != null ? this.changeLogFilter.apply(changeLogClass) : true)
        .map(this::buildChangeLogObject)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private List<Class<?>> fetchChangeLogClassesSorted() {
    List<Class<?>> changeLogs = new ArrayList<>(new Reflections(changeLogsBasePackageList).getTypesAnnotatedWith(ChangeLog.class)); // TODO remove dependency, do own method
    changeLogs.sort(new ChangeLogComparator());
    return changeLogs;
  }

  private ChangeLogItem buildChangeLogObject(Class<?> type) {
    try {
      return new ChangeLogItem(type, type.getConstructor().newInstance(), type.getAnnotation(ChangeLog.class).order(), fetchChangeSetFromClass(type));
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
        .map(method -> {
          ChangeSet ann = method.getAnnotation(ChangeSet.class);
          return new ChangeSetItem(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), method);
        })
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private List<Method> fetchChangeSetMethodsSorted(final Class<?> type) throws ChangockException {
    final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));
    changeSets.sort(new ChangeSetComparator());
    return changeSets;
  }


  private List<Method> filterChangeSetAnnotation(List<Method> allMethods) throws ChangockException {
    final Set<String> changeSetIds = new HashSet<>();
    final List<Method> changeSetMethods = new ArrayList<>();
    for (final Method method : allMethods) {
      if (method.isAnnotationPresent(ChangeSet.class)) {
        String id = method.getAnnotation(ChangeSet.class).id();
        if (changeSetIds.contains(id)) {
          throw new ChangockException(String.format("Duplicated changeset id found: '%s'", id));
        }
        changeSetIds.add(id);
        if (isChangeSetWithinSystemVersionRange(method.getAnnotation(ChangeSet.class))) {
          changeSetMethods.add(method);
        }
      }
    }
    return changeSetMethods;
  }

  //todo Create a SystemVersionChecker
  private boolean isChangeSetWithinSystemVersionRange(ChangeSet changeSetAnn) {
    boolean isWithinVersion = false;
    String versionString = changeSetAnn.systemVersion();
    ArtifactVersion version = new DefaultArtifactVersion(versionString);
    if (version.compareTo(startSystemVersion) >= 0 && version.compareTo(endSystemVersion) <= 0) {
      isWithinVersion = true;
    }
    return isWithinVersion;
  }


  private static class ChangeLogComparator implements Comparator<Class<?>>, Serializable {
    private static final long serialVersionUID = -358162121872177974L;

    @Override
    public int compare(Class<?> o1, Class<?> o2) {
      ChangeLog c1 = o1.getAnnotation(ChangeLog.class);
      ChangeLog c2 = o2.getAnnotation(ChangeLog.class);

      String val1 = !(StringUtils.hasText(c1.order())) ? o1.getCanonicalName() : c1.order();
      String val2 = !(StringUtils.hasText(c2.order())) ? o2.getCanonicalName() : c2.order();

      if (val1 == null && val2 == null) {
        return 0;
      } else if (val1 == null) {
        return -1;
      } else if (val2 == null) {
        return 1;
      }

      return val1.compareTo(val2);
    }
  }

  private static class ChangeSetComparator implements Comparator<Method>, Serializable {
    private static final long serialVersionUID = -854690868262484102L;

    @Override
    public int compare(Method o1, Method o2) {
      ChangeSet c1 = o1.getAnnotation(ChangeSet.class);
      ChangeSet c2 = o2.getAnnotation(ChangeSet.class);
      return c1.order().compareTo(c2.order());
    }
  }

}
