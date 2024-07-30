package io.mapsmessaging.selector.operators.functions.ml;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.functions.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.ModelUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMLModelOperation extends AbstractModelOperations {
  protected final List<Instance> dataBuffer = new ArrayList<>();
  protected final long sampleSize;
  protected final long sampleTime;
  protected Instances structure;

  protected AbstractMLModelOperation(String modelName, List<String> identity, long time, long samples) {
    super(modelName, identity);
    this.sampleSize = samples;
    this.sampleTime = (time > 0) ?System.currentTimeMillis() + time:0;
    try {
      initializeModel();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  protected void initializeModel() throws Exception {
    initializeSpecificModel();
    if (MLFunction.getModelStore().modelExists(modelName)) {
      byte[] loadedModel = MLFunction.getModelStore().loadModel(modelName);
      structure = ModelUtils.byteArrayToInstances(loadedModel);
      buildModel(structure);
      isModelTrained = true;
    } else {
      ArrayList<Attribute> attributes = new ArrayList<>();
      for (String s : identity) {
        attributes.add(new Attribute(s));
      }
      structure = new Instances(modelName, attributes, 0);
    }
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    try {
      Instance instance = new DenseInstance(1.0, evaluateList(resolver));
      instance.setDataset(structure);

      // Buffer the instance
      dataBuffer.add(instance);

      if (!isModelTrained &&
          (
              (dataBuffer.size() >= sampleSize ) ||
              (sampleTime != 0 && System.currentTimeMillis() > sampleTime)
          )
      ) {
        Instances trainingData = new Instances(structure, dataBuffer.size());
        trainingData.addAll(dataBuffer);
        buildModel(trainingData);
        dataBuffer.clear();
        if(isModelTrained){
          MLFunction.getModelStore().saveModel(modelName, ModelUtils.instancesToByteArray(trainingData));
        }
      }

      if (isModelTrained) {
        return applyModel(instance);
      }
    } catch (Exception e) {
      ParseException ex = new ParseException("Error evaluating " + getClass().getSimpleName());
      ex.initCause(e);
      throw ex;
    }
    return Double.NaN; // or some other placeholder
  }

  protected abstract void initializeSpecificModel() throws Exception;

  protected abstract double applyModel(Instance instance) throws Exception;

  protected abstract void buildModel(Instances trainingData) throws Exception;

}

