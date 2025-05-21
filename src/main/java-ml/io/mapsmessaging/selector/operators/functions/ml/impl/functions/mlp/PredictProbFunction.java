package io.mapsmessaging.selector.operators.functions.ml.impl.functions.mlp;

import smile.classification.MLP;

public class PredictProbFunction implements MLPFunction {

  @Override
  public double compute(MLP mlp, double[] data) {
    double[] posteriori = new double[mlp.numClasses()];
    mlp.predict(data, posteriori);
    double max = Double.NEGATIVE_INFINITY;
    for (double p : posteriori) {
      if (p > max) max = p;
    }
    return max;
  }

  @Override
  public String getName() {
    return "predictprob";
  }
}
