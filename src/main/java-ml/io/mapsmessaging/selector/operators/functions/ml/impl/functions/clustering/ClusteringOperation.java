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
import io.mapsmessaging.selector.ml.ModelStore;
import io.mapsmessaging.selector.operators.functions.ml.RawDataMLModelOperation;
import java.io.IOException;
import java.util.List;
import smile.clustering.*;
import smile.data.DataFrame;

public abstract class ClusteringOperation extends RawDataMLModelOperation {
  protected final KMeansFunction kmeansFunction;
  private CentroidClustering<double[], double[]> kmeans;

  protected ClusteringOperation(
      String modelName, String operationName, List<String> identity, long time, long samples,  ModelStore modelStore)
      throws ModelException, IOException {
    super(modelName, identity, time, samples, modelStore);
    this.kmeansFunction = computeFunction(operationName);
  }

  private static KMeansFunction computeFunction(String operationName) throws ModelException {
    if (operationName.equalsIgnoreCase("distance")) {
      return new DistanceFunction();
    } else if (operationName.equalsIgnoreCase("clusterlabel")) {
      return new ClusterLabelFunction();
    } else if (operationName.startsWith("centroid[")) {
      int index = Integer.parseInt(operationName.substring(9, operationName.length() - 1));
      return new CentroidFunction(index);
    } else if (operationName.equalsIgnoreCase("wcss")) {
      return new WCSSFunction();
    } else if (operationName.startsWith("clustersizes[")) {
      int index = Integer.parseInt(operationName.substring(13, operationName.length() - 1));
      return new ClusterSizesFunction(index);
    } else if (operationName.equalsIgnoreCase("totalclusters")) {
      return new TotalClustersFunction();
    }
    throw new ModelException("Expected <distance>, <clusterlabel>, <centroid>, <wcss>, <clustersizes> or <totalclusters> received [" + operationName+"]");
  }

  @Override
  protected void initializeSpecificModel() {
    // No-op for Smile
  }

  public void buildModel(DataFrame dataFrame) {
    kmeans = createClusterMeans(dataFrame.toArray(), new Clustering.Options(3, 10));
    isModelTrained = true;
  }

  public double applyModel(double[] input) {
    return kmeansFunction.compute(kmeans, input);
  }

  public abstract CentroidClustering<double[], double[]> createClusterMeans(
      double[][] data, Clustering.Options options);
}
