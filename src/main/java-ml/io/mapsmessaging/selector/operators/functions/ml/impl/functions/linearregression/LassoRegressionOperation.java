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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.linearregression;

import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.List;

import io.mapsmessaging.selector.operators.functions.ml.ModelStore;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.regression.LASSO;
import smile.regression.LinearModel;

@SuppressWarnings("java:S110") // Yes we go to 6 deep, but this can not be helped when integrating ML
public class LassoRegressionOperation extends LinearRegressionOperation {

  public LassoRegressionOperation(
      String modelName, String operationName, List<String> identity, long time, long samples, ModelStore modelStore)
      throws ModelException, IOException {
    super(modelName, operationName, identity, time, samples, modelStore);
  }

  @Override
  protected LinearModel generate(Formula formula, DataFrame dataFrame) {
    return LASSO.fit(formula, dataFrame);
  }

  @Override
  protected String getName() {
    return "lasso";
  }
}
