package io.mapsmessaging.selector.operators.functions.ml.impl.functions.lda;

import smile.classification.LDA;

public interface LDAFunction {
  double compute(LDA lda, double[] data);
  String getName();
}

