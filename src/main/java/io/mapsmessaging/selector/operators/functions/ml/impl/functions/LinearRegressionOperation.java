package io.mapsmessaging.selector.operators.functions.ml.impl.functions;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class LinearRegressionOperation extends AbstractMLModelOperation {
  private LinearRegression linearRegression;

  public LinearRegressionOperation(String modelName, List<String> identity, long time, long samples) {
    super(modelName, identity, time, samples);
  }

  @Override
  protected void initializeSpecificModel() throws Exception {
    // Adding attributes based on the identity
    ArrayList<Attribute> attributes = new ArrayList<>();
    for (String s : identity) {
      attributes.add(new Attribute(s));
    }
    // Adding target attribute for regression
    attributes.add(new Attribute("target"));
    structure = new Instances(modelName, attributes, 0);
    structure.setClassIndex(structure.numAttributes() - 1);
    linearRegression = new LinearRegression();
  }

  @Override
  protected void buildModel(Instances trainingData) throws Exception {
    linearRegression.buildClassifier(trainingData);
    isModelTrained = true;
  }

  @Override
  protected double applyModel(Instance instance) throws Exception {
    return linearRegression.classifyInstance(instance);
  }

  @Override
  public String toString() {
    return "LinearRegression(" + super.toString() + ")";
  }

}
