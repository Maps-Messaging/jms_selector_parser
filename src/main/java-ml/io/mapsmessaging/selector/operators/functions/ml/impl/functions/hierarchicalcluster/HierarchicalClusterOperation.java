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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.hierarchicalcluster;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.WardLinkage;
import smile.data.DataFrame;

public class HierarchicalClusterOperation extends AbstractMLModelOperation  {

  private double[][] centroids;

  public HierarchicalClusterOperation(String modelName, List<String> identity, long time, long samples) throws ModelException, IOException {
    super(modelName, identity, time, samples);
  }

  @Override
  protected void initializeSpecificModel() {
  }


  @Override
  public String toString() {
    return "hierarchical(" + super.toString() + ")";
  }

  @Override
  public void buildModel(DataFrame dataFrame) {
    String[] names = dataFrame.names();
    if(identity.isEmpty()){
      identity.addAll(Arrays.asList(names).subList(0, names.length - 1));
    }
    double[][] data = normalize(dataFrame.toArray());
    WardLinkage linkage = WardLinkage.of(data);
    HierarchicalClustering hierarchicalClusterer = HierarchicalClustering.fit(linkage);

    int[] labels = hierarchicalClusterer.partition(3);

    Map<Integer, Integer> clusterMap = new HashMap<>();
    int clusterIndex = 0;
    for (int label : labels) {
      if (!clusterMap.containsKey(label)) {
        clusterMap.put(label, clusterIndex++);
      }
    }
    centroids = new double[clusterMap.size()][data[0].length];
    int[] counts = new int[clusterMap.size()];

    for (int i = 0; i < data.length; i++) {
      int cluster = clusterMap.get(labels[i]);
      for (int j = 0; j < data[i].length; j++) {
        centroids[cluster][j] += data[i][j];
      }
      counts[cluster]++;
    }

    for (int i = 0; i < centroids.length; i++) {
      for (int j = 0; j < centroids[i].length; j++) {
        centroids[i][j] /= counts[i];
      }
    }

  }

  private double[][] normalize(double[][] data) {
    int cols = data[0].length;
    double[] mean = new double[cols];
    double[] std = new double[cols];

    for (double[] row : data) {
      for (int j = 0; j < cols; j++) {
        mean[j] += row[j];
      }
    }
    for (int j = 0; j < cols; j++) {
      mean[j] /= data.length;
    }
    for (double[] row : data) {
      for (int j = 0; j < cols; j++) {
        double diff = row[j] - mean[j];
        std[j] += diff * diff;
      }
    }
    for (int j = 0; j < cols; j++) {
      std[j] = Math.sqrt(std[j] / data.length);
      if (std[j] == 0) std[j] = 1;
    }

    double[][] normalized = new double[data.length][cols];
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < cols; j++) {
        normalized[i][j] = (data[i][j] - mean[j]) / std[j];
      }
    }
    return normalized;
  }


  @Override
  public double applyModel(double[] data) {
    double minDistance = Double.MAX_VALUE;
    int closestCluster = -1;

    for (int i = 0; i < centroids.length; i++) {
      double dist = 0.0;
      for (int j = 0; j < data.length; j++) {
        double diff = data[j] - centroids[i][j];
        dist += diff * diff;
      }
      if (dist < minDistance) {
        minDistance = dist;
        closestCluster = i;
      }
    }

    return closestCluster;
  }


}
