package io.mapsmessaging.selector.operators.functions.ml.impl.functions;

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
