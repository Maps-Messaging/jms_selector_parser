package io.mapsmessaging.selector.operators.functions.ml.impl.functions;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
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
  protected void buildModel(Instances trainingData) throws Exception {
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
