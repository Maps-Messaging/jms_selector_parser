package io.mapsmessaging.selector.operators.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.Operation;
import io.mapsmessaging.selector.operators.functions.ml.*;

import java.util.List;

public class MLFunction extends Operation {
  private final String functionName;
  private final String modelName;
  private final long sampleSize;
  private final long sampleTime;
  private final List<String> identifiers;

  public MLFunction(String functionName, List<String> list) {
    this.functionName = functionName;
    this.modelName = list.get(0);
    identifiers = list.subList(1, list.size());

    sampleSize = 100;
    sampleTime = 0;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    return this;
  }

  @Override
  public Object compile() {
    switch (functionName) {
      case "K-means_clustering":
        return new KMeansClusterOperation(modelName, identifiers, sampleTime, sampleSize);
      case "linear_regression":
        return new LinearRegressionOperation(modelName, identifiers, sampleTime, sampleSize);
      case "decision_tree":
        return new DecisionTreeOperation(modelName, identifiers, sampleTime, sampleSize);
      case "naive_bayes":
        return new NaiveBayesOperation(modelName, identifiers, sampleTime, sampleSize);
      case "hierarchical_clustering":
        return new HierarchicalClusterOperation(modelName,identifiers, sampleTime, sampleSize);
      case "pca":
        return new PCAOperation(modelName, identifiers, sampleTime, sampleSize);
      default:
        throw new UnsupportedOperationException("Unknown ML function: " + functionName);
    }
  }


  public String toString(){
    return functionName+"("+modelName+")";
  }

}