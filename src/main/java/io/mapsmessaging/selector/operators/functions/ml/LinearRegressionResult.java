package io.mapsmessaging.selector.operators.functions.ml;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LinearRegressionResult {
  private final double prediction;

  public LinearRegressionResult(double prediction) {
    this.prediction = prediction;
  }
}
