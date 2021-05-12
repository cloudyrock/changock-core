package com.github.cloudyrock.springboot.profiles.withinterfaceparameter;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.springboot.util.ClassNotInterfaced;
import com.github.cloudyrock.springboot.util.InterfaceDependency;

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


  @ChangeSet(author = "executor", id = "withNonLockGuardedParameter", order = "3")
  public void withNonLockGuardedParameter(@NonLockGuarded InterfaceDependency dependency) {
    dependency.getInstance().getValue();
  }



}
