package com.github.cloudyrock.springboot.v2_4;


public final class MongockSpringbootV2_4 {


  //TODO javadoc
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends SpringbootV2_4BuilderBase<Builder> {
    @Override
    protected Builder getInstance() {
      return this;
    }
  }


}
