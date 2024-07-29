package io.mapsmessaging.selector.operators.functions.ml;

import lombok.Getter;
import lombok.ToString;
import weka.core.Instance;

@Getter
@ToString
public class KMeansResult {
  private final double distance;
  private final Instance centroidInstance;
  private final Instance instance;

  public KMeansResult(double distance, Instance centroidInstance, Instance instance) {
    this.distance = distance;
    this.centroidInstance = centroidInstance;
    this.instance = instance;
  }
}
