/*
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.pca;

import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import weka.attributeSelection.PrincipalComponents;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.core.Instance;

public class ExplainedVarianceFunction implements PCAFunction {
  @Override
  public double compute(AttributeSelection filter, Instance instance) throws ModelException {
    PrincipalComponents pca = (PrincipalComponents) filter.getEvaluator();
    return pca.getVarianceCovered();
  }

  @Override
  public String getName() {
    return "explainedvariance";
  }
}

