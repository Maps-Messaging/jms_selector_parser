package io.mapsmessaging.selector.operators.functions.ml;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class NaiveBayesOperation extends AbstractMLModelOperation {
  private NaiveBayes naiveBayes;

  public NaiveBayesOperation(String modelName, List<String> identity, long time, long samples) {
    super(modelName, identity, time, samples);
  }

  @Override
  protected void initializeSpecificModel() throws Exception {
    // Adding attributes based on the identity
    ArrayList<Attribute> attributes = new ArrayList<>();
    for (String s : identity) {
      attributes.add(new Attribute(s));
    }
    structure = new Instances(modelName, attributes, 0);
    structure.setClassIndex(structure.numAttributes() - 1);
    naiveBayes = new NaiveBayes();
  }

  @Override
  protected void trainModel() throws Exception {
    Instances trainingData = new Instances(structure, dataBuffer.size());
    trainingData.addAll(dataBuffer);
    naiveBayes.buildClassifier(trainingData);
    isModelTrained = true;
    dataBuffer.clear();
  }

  @Override
  protected double applyModel(Instance instance) throws Exception {
    return naiveBayes.classifyInstance(instance);
  }

  @Override
  public String toString() {
    return "NaiveBayes(" + super.toString() + ")";
  }
}
