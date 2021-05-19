package com.github.cloudyrock.mongock.runner.core.executor.operation.list;

import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;

import java.util.List;

public class ListOp extends Operation<List> {

  public static final String ID = "LIST";

  public ListOp() {
    super(ID);
  }
}
