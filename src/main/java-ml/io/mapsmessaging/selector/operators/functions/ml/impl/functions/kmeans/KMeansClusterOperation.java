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

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class KMeansClusterOperation extends AbstractMLModelOperation {
  private SimpleKMeans kmeans;
  private final KMeansFunction kmeansFunction;

  public KMeansClusterOperation(String modelName, String operationName, List<String> identity, long time, long samples) throws ModelException, IOException {
    super(modelName, identity, time, samples);
    kmeansFunction = computeFunction(operationName);
  }

  protected void initializeSpecificModel() throws ModelException {
    ArrayList<Attribute> attributes = new ArrayList<>();
    for (String s : identity) {
      attributes.add(new Attribute(s));
    }
    structure = new Instances(modelName, attributes, 0);
    kmeans = new SimpleKMeans();
    try {
      kmeans.setNumClusters(3);
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  protected void buildModel(Instances trainingData) throws ModelException {
    try {
      kmeans.buildClusterer(trainingData);
      isModelTrained = true;
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  protected double applyModel(Instance instance) throws ModelException {
    try {
      return kmeansFunction.compute(kmeans, instance);
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  public String toString() {
    return "K-means_clustering("+kmeansFunction.getName()+", " + super.toString() + ")";
  }

  private static KMeansFunction computeFunction(String operationName){
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
    } else if (operationName.equalsIgnoreCase("silhouettescore")) {
      return new SilhouetteScoreFunction();
    }
    return new DistanceFunction();
  }

}
