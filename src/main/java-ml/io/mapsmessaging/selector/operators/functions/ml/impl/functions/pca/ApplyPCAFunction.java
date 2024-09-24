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
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class ApplyPCAFunction implements PCAFunction {
  private final int index;

  public ApplyPCAFunction(int index) {
    this.index = index;
  }

  @Override
  public double compute(AttributeSelection filter, Instance instance) throws ModelException {
    try {
      Instances instanceData = new Instances(instance.dataset(), 0);
      instanceData.add(instance);
      Instances transformedData = Filter.useFilter(instanceData, filter);

      // Ensure the index is within the valid range
      if (index < 0 || index >= transformedData.numAttributes()) {
        throw new IllegalArgumentException("Invalid index: " + index);
      }
      return transformedData.firstInstance().value(index); // Return the specified principal component
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  public String getName() {
    return "applypca[" + index + "]";
  }
}
