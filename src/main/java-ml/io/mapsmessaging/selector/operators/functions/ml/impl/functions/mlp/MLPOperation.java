package io.mapsmessaging.selector.operators.functions.ml.impl.functions.mlp;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import smile.classification.MLP;
import smile.data.DataFrame;

public class MLPOperation extends AbstractMLModelOperation {
  private final MLPFunction operation;
  private MLP mlp;

  public MLPOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    this.operation = computeFunction(operationName);
  }

  private static MLPFunction computeFunction(String op) {
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

    Properties props = new Properties();
    props.setProperty("smile.mlp.layers", "ReLU(100)");
    props.setProperty("smile.mlp.epochs", "50");
    props.setProperty("smile.mlp.mini_batch", "16");

    mlp = MLP.fit(x.toArray(), labels, props);
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data) {
    return operation.compute(mlp, data);
  }

  @Override
  public String toString() {
    return "mlp (" + operation.getName() + ", " + super.toString() + ")";
  }
}
