package io.mapsmessaging.selector.operators.functions.ml.impl.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.functions.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.AbstractModelOperations;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.ModelUtils;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.types.TFloat64;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

public class TensorFlowOperation extends AbstractModelOperations {
  private SavedModelBundle model;

  public TensorFlowOperation(String modelName, List<String> identity) {
    super(modelName, identity);
    try {
      initializeModel();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void initializeModel() throws Exception {
    loadModel();
    isModelTrained = true;
  }

  protected Object[] evaluateList(IdentifierResolver resolver) throws ParseException {
    Object[] dataset = new Object[identity.size()];
    for (int x = 0; x < identity.size(); x++) {
      dataset[x] = resolver.get(identity.get(x));
    }
    return dataset;
  }


  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object[] features = evaluateList(resolver);

    Tensor<TFloat64> inputTensor = createTensor(features);

    // Run the model and fetch the result
    try (Session session = model.session()) {
      List<Tensor<?>> outputs = session.runner()
          .feed("input_tensor_name", inputTensor)
          .fetch("output_tensor_name")
          .run();

      // Retrieve the output tensor and extract the result
      try (Tensor<Double> outputTensor = outputs.get(0).expect(double.class)) {
        double[][] result = new double[1][];
        outputTensor.copyTo(result);
        return result[0][0];
      }
    }
  }

  protected void loadModel() throws Exception {
    byte[] modelData = MLFunction.getModelStore().loadModel(modelName + "_data");
    model = ModelUtils.byteArrayToModel(modelData, "");
    isModelTrained = true;
  }

  private Tensor<TFloat64> createTensor(Object[] features) {
    double[] doubleFeatures = new double[features.length];

    for (int i = 0; i < features.length; i++) {
      Object feature = features[i];
      if (feature instanceof Number) {
        doubleFeatures[i] = ((Number) feature).doubleValue();
      } else if (feature instanceof String) {
        try {
          doubleFeatures[i] = Double.parseDouble((String) feature);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Invalid string input: " + feature, e);
        }
      } else {
        doubleFeatures[i] = Double.NaN;
      }
    }

    return TFloat64.tensorOf(NdArrays.vectorOf(doubleFeatures));
  }

}


