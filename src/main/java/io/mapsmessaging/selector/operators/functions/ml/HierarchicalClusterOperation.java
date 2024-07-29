package io.mapsmessaging.selector.operators.functions.ml;

import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class HierarchicalClusterOperation extends AbstractMLModelOperation {
  private HierarchicalClusterer hierarchicalClusterer;

  public HierarchicalClusterOperation(String modelName, List<String> identity, long time, long samples) {
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
    hierarchicalClusterer = new HierarchicalClusterer();
  }

  @Override
  protected void trainModel() throws Exception {
    Instances trainingData = new Instances(structure, dataBuffer.size());
    trainingData.addAll(dataBuffer);
    hierarchicalClusterer.buildClusterer(trainingData);
    isModelTrained = true;
    dataBuffer.clear();
  }

  @Override
  protected double applyModel(Instance instance) throws Exception {
    return hierarchicalClusterer.clusterInstance(instance);
  }

  @Override
  public String toString() {
    return "HierarchicalCluster(" + super.toString() + ")";
  }
}
