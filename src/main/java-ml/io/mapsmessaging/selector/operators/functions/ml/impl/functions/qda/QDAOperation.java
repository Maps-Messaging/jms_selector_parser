package io.mapsmessaging.selector.operators.functions.ml.impl.functions.qda;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import smile.classification.QDA;
import smile.data.DataFrame;

public class QDAOperation extends AbstractMLModelOperation {
  private final QDAFunction operation;
  private QDA qda;

  public QDAOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    this.operation = computeFunction(operationName);
  }

  private static QDAFunction computeFunction(String op) {
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
    qda = QDA.fit(x.toArray(), labels);
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data) {
    return operation.compute(qda, data);
  }

  @Override
  public String toString() {
    return "qda (" + operation.getName() + ", " + super.toString() + ")";
  }
}
