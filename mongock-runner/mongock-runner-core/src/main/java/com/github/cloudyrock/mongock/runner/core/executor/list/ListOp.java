package com.github.cloudyrock.mongock.runner.core.executor.list;

import com.github.cloudyrock.mongock.runner.core.executor.Operation;

import java.util.List;

public class ListOp extends Operation<List> {

  public static final String ID = "LIST";

  public ListOp() {
    super(ID);
  }
}
