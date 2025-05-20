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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.clustering;

import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import smile.clustering.CentroidClustering;

public class ClusterSizesFunction implements KMeansFunction {

  private final int index;

  public ClusterSizesFunction(int index) {
    this.index = index;
  }

  @Override
  public double compute(CentroidClustering<double[], double[]> model, double[] instance)
      throws ModelException {
    try {
      int[] sizes = model.size();
      if (index < 0 || index >= sizes.length) {
        throw new IndexOutOfBoundsException("Cluster index out of bounds: " + index);
      }
      return sizes[index];
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  public String getName() {
    return "clustersizes[" + index + "]";
  }
}
