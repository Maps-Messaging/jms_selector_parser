package io.mapsmessaging.selector.operators.functions.ml.impl.functions.qda;

import smile.classification.QDA;

public class PredictProbFunction implements QDAFunction {

  @Override
  public double compute(QDA qda, double[] data) {
    double[] posteriori = new double[qda.numClasses()];
    qda.predict(data, posteriori);
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
