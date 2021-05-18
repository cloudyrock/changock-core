package com.github.cloudyrock.mongock.runner.core.executor.change;

import com.github.cloudyrock.mongock.runner.core.executor.Operation;

public  class MigrationOp extends Operation<Boolean> {

  public static final String ID = "MIGRATION";

  public MigrationOp() {
    super(ID);
  }



}
