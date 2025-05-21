package io.mapsmessaging.selector.operators.functions.ml.impl.functions.qda;

import smile.classification.QDA;

public class PredictFunction implements QDAFunction {

  @Override
  public double compute(QDA qda, double[] data) {
    return qda.predict(data);
  }

  @Override
  public String getName() {
    return "predict";
  }
}
