package io.mapsmessaging.selector.operators.functions.ml.impl.functions.lda;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import smile.classification.LDA;
import smile.data.DataFrame;

public class LDAOperation extends AbstractMLModelOperation {
  private final LDAFunction operation;
  private LDA lda;

  public LDAOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    this.operation = computeFunction(operationName);
  }

  private static LDAFunction computeFunction(String op) {
    switch (op.toLowerCase()) {
      case "predictprob":
        return new PredictProbFunction();
      case "predict":
      default:
        return new PredictFunction();
    }
  }

  @Override
  protected void initializeSpecificModel() {}

  @Override
  public void buildModel(DataFrame data) {
    String labelColumn = data.schema().field(data.ncol() - 1).name();
    int[] labels = data.column(labelColumn).toIntArray();
    DataFrame x = data.drop(labelColumn);

    if (identity.isEmpty()) {
      identity.addAll(Arrays.asList(x.names()));
    }
    lda = LDA.fit(x.toArray(), labels);
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data) {
    return operation.compute(lda, data);
  }

  @Override
  public String toString() {
    return "lda (" + operation.getName() + ", " + super.toString() + ")";
  }
}
