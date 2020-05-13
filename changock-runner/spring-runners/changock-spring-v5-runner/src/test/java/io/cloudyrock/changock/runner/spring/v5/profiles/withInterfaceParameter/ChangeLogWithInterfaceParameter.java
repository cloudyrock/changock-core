package io.cloudyrock.changock.runner.spring.v5.profiles.withInterfaceParameter;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import io.cloudyrock.changock.runner.spring.v5.util.ClassNotInterfaced;
import io.cloudyrock.changock.runner.spring.v5.util.InterfaceDependency;

@ChangeLog(order = "0")
public class ChangeLogWithInterfaceParameter {


  @ChangeSet(author = "executor", id = "withInterfaceParameter", order = "1")
  public void withInterfaceParameter(InterfaceDependency dependency) {
    dependency.getValue();
  }

  @ChangeSet(author = "executor", id = "withInterfaceParameter2", order = "1")
  public void withInterfaceParameter2(InterfaceDependency dependency) {
    dependency.getInstance().getValue();
  }


  @ChangeSet(author = "executor", id = "withClassNotInterfacedParameter", order = "1")
  public void withClassNotInterfacedParameter(ClassNotInterfaced dependency) {
  }





}
