/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2025 ] MapsMessaging B.V.
 *
 *  Licensed under the Apache License, Version 2.0 with the Commons Clause
 *  (the "License"); you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      https://commonsclause.com/
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.mapsmessaging.selector.operators.functions.ml;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.functions.ml.impl.SmileFunction;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.ModelUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import smile.data.DataFrame;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public abstract class AbstractMLModelOperation extends AbstractModelOperations {
  protected final List<Instance> dataBuffer = new ArrayList<>();
  protected final long sampleSize;
  protected final long sampleTime;
  protected Instances structure;

  protected AbstractMLModelOperation(String modelName, List<String> identity, long time, long samples) throws ModelException, IOException {
    super(modelName, identity);
    this.sampleSize = samples;
    this.sampleTime = (time > 0) ? System.currentTimeMillis() + time : 0;
    initializeModel();
  }

  @SuppressWarnings(" java:S112") // This is thrown from the underlying library, nothing we can do here
  // NOSONAR: This is thrown from the underlying library, nothing we can do here
  protected void initializeModel() throws IOException, ModelException {
    initializeSpecificModel();
    if (MLFunction.getModelStore().modelExists(modelName)) {
      byte[] loadedModel = MLFunction.getModelStore().loadModel(modelName);
      if(this instanceof SmileFunction) {
        DataFrame dataFrame = ModelUtils.dataFrameFromBytes(loadedModel, null);
        ((SmileFunction)this).buildModel(dataFrame);
      } else {
        structure = ModelUtils.byteArrayToInstances(loadedModel);
        buildModel(structure);
      }
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
  // NOSONAR: This is thrown from the underlying library, nothing we can do here
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    try {
      double[] data = evaluateList(resolver);
      Instance instance = new DenseInstance(1.0, data);
      instance.setDataset(structure);

      // Buffer the instance

      if (!isModelTrained){
        dataBuffer.add(instance);
        if( ((dataBuffer.size() >= sampleSize) ||
            (sampleTime != 0 && System.currentTimeMillis() > sampleTime))) {
          Instances trainingData = new Instances(structure, dataBuffer.size());
          trainingData.addAll(dataBuffer);
          buildModel(trainingData);
          dataBuffer.clear();
          if (isModelTrained) {
            MLFunction.getModelStore().saveModel(modelName, ModelUtils.instancesToByteArray(trainingData));
          }
        }
      }
      if (isModelTrained) {
        if(this instanceof SmileFunction) {
          return ((SmileFunction)this).applyModel(data);
        }
        return applyModel(instance);
      }
    } catch (Exception e) {
      ParseException ex = new ParseException("Error evaluating " + getClass().getSimpleName());
      ex.initCause(e);
      throw ex;
    }
    return Double.NaN; // or some other placeholder
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

  protected double[] convertInstanceToDoubleArray(Instance instance) {
    double[] values = new double[instance.numAttributes()];
    for (int i = 0; i < instance.numAttributes(); i++) {
      values[i] = instance.value(i);
    }
    return values;
  }

  protected abstract void initializeSpecificModel() throws ModelException;

  protected double applyModel(Instance instance) throws ModelException{
    return 0;
  }

  protected void buildModel(Instances trainingData) throws ModelException{

  }
}
