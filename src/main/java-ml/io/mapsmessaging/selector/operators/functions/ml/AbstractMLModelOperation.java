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
import io.mapsmessaging.selector.operators.functions.ml.impl.store.ModelUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import smile.data.DataFrame;

public abstract class AbstractMLModelOperation extends AbstractModelOperations {
  protected final List<double[]> dataBuffer = new ArrayList<>();

  protected final long sampleSize;
  protected final long sampleTime;

  protected AbstractMLModelOperation(String modelName, List<String> identity, long time, long samples) throws ModelException, IOException {
    super(modelName, identity);
    this.sampleSize = samples;
    this.sampleTime = (time > 0) ? System.currentTimeMillis() + time : 0;
    initializeModel();
  }

  protected void initializeModel() throws IOException, ModelException {
    initializeSpecificModel();
    if (MLFunction.getModelStore().modelExists(modelName)) {
      byte[] loadedModel = MLFunction.getModelStore().loadModel(modelName);
      DataFrame dataFrame = ModelUtils.dataFrameFromBytes(loadedModel, null);
      buildModel(dataFrame);
      if(identity.isEmpty()) {
        identity.addAll(Arrays.asList(dataFrame.names()));
      }
      isModelTrained = true;
    }
    else if(requiresLabel() && identity.isEmpty()) {
      throw new ModelException("This operation requires a trained model or labeled input data, but neither was provided.");
    }
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    try {
      double[] data = evaluateList(resolver);
      if (!isModelTrained){
        dataBuffer.add(data);
        if( ((dataBuffer.size() >= sampleSize) ||
            (sampleTime != 0 && System.currentTimeMillis() > sampleTime))) {
          DataFrame df = ModelUtils.dataFrameFromBuffer(dataBuffer, identity);
          buildModel(df);
          if (isModelTrained) {
            MLFunction.getModelStore().saveModel(modelName, ModelUtils.dataFrameToBytes(df, null));
          }
          dataBuffer.clear();
        }
      }
      if (isModelTrained) {
        return applyModel(data);
      }
    } catch (Exception e) {
      ParseException ex = new ParseException("Error evaluating " + getClass().getSimpleName());
      ex.initCause(e);
      throw ex;
    }
    return Double.NaN;
  }

  protected double[] evaluateList(IdentifierResolver resolver) throws ParseException {
    if(identity.isEmpty()) {
      identity.addAll(resolver.getKeys());
    }

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




  protected abstract void initializeSpecificModel() throws ModelException;

  protected abstract double applyModel(double[] data);

  protected abstract void buildModel(DataFrame dataFrame) throws ModelException;

  public abstract boolean requiresLabel();
}
