package io.mapsmessaging.selector.operators.functions.ml;

import weka.attributeSelection.PrincipalComponents;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.util.ArrayList;
import java.util.List;

public class PCAOperation extends AbstractMLModelOperation {
  private AttributeSelection filter;

  public PCAOperation(String modelName, List<String> identity, long time, long samples) {
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
    filter = new AttributeSelection();
  }

  @Override
  protected void trainModel() throws Exception {
    // PCA does not require traditional training, it transforms the data
    Instances trainingData = new Instances(structure, dataBuffer.size());
    trainingData.addAll(dataBuffer);

    // Set up the PrincipalComponents evaluator
    PrincipalComponents pca = new PrincipalComponents();
    pca.setVarianceCovered(0.95); // For example, keep 95% of variance

    // Set up the Ranker search method
    Ranker ranker = new Ranker();
    ranker.setNumToSelect(-1);

    // Set up the AttributeSelection filter
    filter.setEvaluator(pca);
    filter.setSearch(ranker);
    filter.setInputFormat(trainingData);

    // Apply the filter to the training data to initialize it
    Filter.useFilter(trainingData, filter);
    isModelTrained = true;
    dataBuffer.clear();
  }

  @Override
  protected double applyModel(Instance instance) throws Exception {
    Instances instanceData = new Instances(structure, 0);
    instanceData.add(instance);
    Instances transformedData = Filter.useFilter(instanceData, filter);
    return transformedData.firstInstance().value(0); // Return the first principal component as an example
  }

  @Override
  public String toString() {
    return "PCA(" + super.toString() + ")";
  }
}
