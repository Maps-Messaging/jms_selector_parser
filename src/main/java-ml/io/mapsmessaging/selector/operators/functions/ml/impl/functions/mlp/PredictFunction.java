package io.mapsmessaging.selector.operators.functions.ml.impl.functions.mlp;

import smile.classification.MLP;

public class PredictFunction implements MLPFunction {

  @Override
  public double compute(MLP mlp, double[] data) {
    return mlp.predict(data);
  }

  @Override
  public String getName() {
    return "predict";
  }
}
