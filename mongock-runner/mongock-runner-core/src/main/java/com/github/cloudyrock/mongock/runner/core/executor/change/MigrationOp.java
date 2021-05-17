package com.github.cloudyrock.mongock.runner.core.executor.change;

import com.github.cloudyrock.mongock.runner.core.executor.Operation;

public final class MigrationOp extends Operation {

  public static final String ID = "MIGRATION";

  public MigrationOp() {
    super(ID);
  }
}
