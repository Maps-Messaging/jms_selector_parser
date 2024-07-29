package io.mapsmessaging.selector.operators.functions.ml;

import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class KMeansClusterOperation extends AbstractMLModelOperation {
  private SimpleKMeans kmeans;

  public KMeansClusterOperation(String modelName, List<String> identity, long time, long samples) {
    super(modelName, identity, time, samples);
  }

  protected void initializeSpecificModel() throws Exception {
    ArrayList<Attribute> attributes = new ArrayList<>();
    for(String s : identity) {
      attributes.add(new Attribute(s));
    }
    structure = new Instances(modelName, attributes, 0);
    kmeans = new SimpleKMeans();
    kmeans.setNumClusters(3);
  }

  @Override
  protected void trainModel() throws Exception {
    Instances trainingData = new Instances(structure, dataBuffer.size());
    trainingData.addAll(dataBuffer);
    kmeans.buildClusterer(trainingData);
    isModelTrained = true;
    dataBuffer.clear();
  }

  @Override
  protected double applyModel(Instance instance) throws Exception {
    Instances centroids = kmeans.getClusterCentroids();
    EuclideanDistance distanceFunction = new EuclideanDistance(centroids);
    Instance centroid = centroids.instance(kmeans.clusterInstance(instance));
    return distanceFunction.distance(centroid, instance);
  }

  @Override
  public String toString() {
    return "K-means_clustering("+super.toString()+ ")";
  }
}
