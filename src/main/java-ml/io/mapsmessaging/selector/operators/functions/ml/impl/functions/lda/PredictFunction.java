package io.mapsmessaging.selector.operators.functions.ml.impl.functions.lda;

import smile.classification.LDA;
import smile.classification.QDA;

public class PredictFunction implements LDAFunction {

  @Override
  public double compute(LDA lda, double[] data) {
    return lda.predict(data);
  }

  @Override
  public String getName() {
    return "predict";
  }
}
