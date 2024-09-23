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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.kmeans;

import lombok.Data;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.EuclideanDistance;

public class SilhouetteScoreFunction implements KMeansFunction {

  @Override
  public double compute(SimpleKMeans kmeans, Instance instance) throws Exception {
    Instances instances = kmeans.getClusterCentroids();  // This should be the actual data, not centroids
    return calculateSilhouetteScore(kmeans, instances);
  }

  private double calculateSilhouetteScore(SimpleKMeans kmeans, Instances instances) throws Exception {
    double totalScore = 0.0;
    int numInstances = instances.numInstances();
    EuclideanDistance distanceFunction = new EuclideanDistance(instances);

    for (int i = 0; i < numInstances; i++) {
      Instance instance = instances.instance(i);
      int cluster = kmeans.clusterInstance(instance);
      double a = averageDistance(kmeans, instance, instances, cluster, distanceFunction);
      double b = nearestClusterDistance(kmeans, instance, instances, cluster, distanceFunction);
      double score = (b - a) / Math.max(a, b);
      totalScore += score;
    }

    return totalScore / numInstances;
  }

  private double averageDistance(SimpleKMeans kmeans, Instance instance, Instances instances, int cluster, EuclideanDistance distanceFunction) throws Exception {
    double sum = 0.0;
    int count = 0;

    for (int i = 0; i < instances.numInstances(); i++) {
      Instance other = instances.instance(i);
      if (kmeans.clusterInstance(other) == cluster && !instance.equals(other)) {
        sum += distanceFunction.distance(instance, other);
        count++;
      }
    }

    return (count == 0) ? 0 : sum / count;
  }

  private double nearestClusterDistance(SimpleKMeans kmeans, Instance instance, Instances instances, int currentCluster, EuclideanDistance distanceFunction) throws Exception {
    double minDistance = Double.MAX_VALUE;

    for (int i = 0; i < kmeans.getNumClusters(); i++) {
      if (i != currentCluster) {
        CalcData data = innerLoop(i, kmeans, instance,instances, distanceFunction);
        double avgDistance = (data.count == 0) ? 0 : data.sum / data.count;
        if (avgDistance < minDistance) {
          minDistance = avgDistance;
        }
      }
    }

    return minDistance;
  }

  private CalcData innerLoop(int i, SimpleKMeans kmeans,Instance instance, Instances instances, EuclideanDistance distanceFunction) throws Exception {
    CalcData data = new CalcData();
    for (int j = 0; j < instances.numInstances(); j++) {
      Instance other = instances.instance(j);
      if (kmeans.clusterInstance(other) == i) {
        data.sum += distanceFunction.distance(instance, other);
        data.count++;
      }
    }
    return data;
  }

  @Override
  public String getName() {
    return "silhouettescore";
  }

  @Data
  private static class CalcData{
    double sum = 0.0;
    int count = 0;
  }
}
