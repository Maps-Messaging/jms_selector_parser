package io.mapsmessaging.selector.operators.functions.ml.impl.functions.lda;

import smile.classification.LDA;

public class PredictProbFunction implements LDAFunction {

  @Override
  public double compute(LDA lda, double[] data) {
    double[] posteriori = new double[lda.numClasses()];
    lda.predict(data, posteriori);
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
