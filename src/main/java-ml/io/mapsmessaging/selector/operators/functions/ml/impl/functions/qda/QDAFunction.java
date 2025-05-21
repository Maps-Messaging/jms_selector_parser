package io.mapsmessaging.selector.operators.functions.ml.impl.functions.qda;

import smile.classification.QDA;

public interface QDAFunction {
  double compute(QDA qda, double[] data);
  String getName();
}

