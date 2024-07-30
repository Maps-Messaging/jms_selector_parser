package io.mapsmessaging.selector.operators.functions.ml.impl.functions;


import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DecisionTreeResult {
  private final double prediction;

  public DecisionTreeResult(double prediction) {
    this.prediction = prediction;
  }
}