package io.mapsmessaging.selector.operators.functions.ml.impl.functions.mlp;

import smile.classification.MLP;

public interface MLPFunction {
  double compute(MLP mlp, double[] data);
  String getName();
}
