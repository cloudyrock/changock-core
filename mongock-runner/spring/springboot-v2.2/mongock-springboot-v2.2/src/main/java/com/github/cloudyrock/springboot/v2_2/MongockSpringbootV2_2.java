package com.github.cloudyrock.springboot.v2_2;


public final class MongockSpringbootV2_2 {


  //TODO javadoc
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends SpringbootV2_2BuilderBase<Builder> {
    @Override
    protected Builder getInstance() {
      return this;
    }
  }


}
