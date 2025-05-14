/*
 *
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *  Copyright [ 2024 - 2025.  ] [Maps Messaging B.V.]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.kmeans;

import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import lombok.Data;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.EuclideanDistance;

public class SilhouetteScoreFunction implements KMeansFunction {

  @Override
  public double compute(SimpleKMeans kmeans, Instance instance) throws ModelException {
    Instances instances = kmeans.getClusterCentroids();  // This should be the actual data, not centroids
    return calculateSilhouetteScore(kmeans, instances);
  }

  private double calculateSilhouetteScore(SimpleKMeans kmeans, Instances instances) throws ModelException {
    try {
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
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  private double averageDistance(SimpleKMeans kmeans, Instance instance, Instances instances, int cluster, EuclideanDistance distanceFunction) throws ModelException {
    try {
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
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  private double nearestClusterDistance(SimpleKMeans kmeans, Instance instance, Instances instances, int currentCluster, EuclideanDistance distanceFunction) throws ModelException {
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

  private CalcData innerLoop(int i, SimpleKMeans kmeans,Instance instance, Instances instances, EuclideanDistance distanceFunction) throws ModelException {
    try {
      CalcData data = new CalcData();
      for (int j = 0; j < instances.numInstances(); j++) {
        Instance other = instances.instance(j);
        if (kmeans.clusterInstance(other) == i) {
          data.sum += distanceFunction.distance(instance, other);
          data.count++;
        }
      }
      return data;
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  public String getName() {
    return "silhouettescore";
  }

  private static class CalcData{
    double sum = 0.0;
    int count = 0;
  }
}
