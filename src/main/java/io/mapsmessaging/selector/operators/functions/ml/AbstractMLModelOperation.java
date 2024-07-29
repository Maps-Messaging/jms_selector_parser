package io.mapsmessaging.selector.operators.functions.ml;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.Operation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMLModelOperation extends Operation {
  protected final List<String> identity;
  protected final List<Instance> dataBuffer = new ArrayList<>();
  protected final String modelName;
  protected final long sampleSize;
  protected final long sampleTime;
  protected Instances structure;
  protected boolean isModelTrained = false;

  protected AbstractMLModelOperation(String modelName, List<String> identity, long time, long samples) {
    this.identity = identity;
    this.modelName = modelName;
    this.sampleSize = samples;
    this.sampleTime = System.currentTimeMillis() + time;
    try {
      initializeModel();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  protected void initializeModel() throws Exception {
    ArrayList<Attribute> attributes = new ArrayList<>();
    for (String s : identity) {
      attributes.add(new Attribute(s));
    }
    structure = new Instances(modelName, attributes, 0);
    initializeSpecificModel();
  }

  protected double[] evaluateList(IdentifierResolver resolver) throws ParseException {
    double[] dataset = new double[identity.size()];
    for (int x = 0; x < identity.size(); x++) {
      Number val = evaluateToNumber(resolver.get(identity.get(x)), resolver);
      if (val != null) {
        dataset[x] = val.doubleValue();
      } else {
        dataset[x] = Double.NaN;
      }
    }
    return dataset;
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
        trainModel();
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

  protected abstract void trainModel() throws Exception;

  protected abstract double applyModel(Instance instance) throws Exception;

  @Override
  public Object compile() {
    return this;
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder(modelName);
    for(String s : identity) {
      sb.append(", ").append(s);
    }
    return sb.toString();
  }
}

