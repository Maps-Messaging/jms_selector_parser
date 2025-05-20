package io.mapsmessaging.selector.operators.functions.ml.impl.functions.pca;

import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import smile.data.DataFrame;
import smile.feature.extraction.PCA;

import java.io.IOException;
import java.util.List;

public class PCAFitOperation extends PCAOperation {

  public PCAFitOperation(String modelName, String operationName, List<String> identity, long time, long samples) throws ModelException, IOException {
    super(modelName, operationName, identity, time, samples);
  }

  @Override
  public PCA create(DataFrame trainingData)  {
    return PCA.fit(trainingData, trainingData.names()).getProjection(trainingData.names().length);
  }

}
