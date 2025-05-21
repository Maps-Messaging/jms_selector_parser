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
import java.io.IOException;
import java.util.List;
import smile.clustering.CentroidClustering;
import smile.clustering.Clustering;
import smile.clustering.GMeans;

public class GMeansClusterOperation extends ClusteringOperation {

  public GMeansClusterOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, operationName, identity, time, samples);
  }

  @Override
  public CentroidClustering<double[], double[]> createClusterMeans(
      double[][] data, Clustering.Options options) {
    return GMeans.fit(data, options);
  }

  @Override
  public String toString() {
    return "g-means (" + kmeansFunction.getName() + ", " + super.toString() + ")";
  }
}
