package io.mapsmessaging.selector.operators.functions.ml.impl.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.functions.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.AbstractModelOperations;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.ModelUtils;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.IOException;
import java.nio.DoubleBuffer;
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

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    double[] features = evaluateList(resolver);

    // Create a TensorFlow tensor from the extracted feature values
    try (Tensor<Double> inputTensor =
             Tensor.create(new long[]{1, features.length}, DoubleBuffer.wrap(features));
         Session session = model.session()) {

      // Run the model and fetch the result
      List<Tensor<?>> outputs = session.runner()
          .feed("input_tensor_name", inputTensor)
          .fetch("output_tensor_name")
          .run();

      // Retrieve the output tensor and extract the result
      try (Tensor<Float> outputTensor = outputs.get(0).expect(Float.class)) {
        float[][] result = new float[1][];
        outputTensor.copyTo(result);
        return result[0][0]; // Adjust this based on your model's output
      }
    }
  }

  protected void saveModel() throws Exception {
    throw new IOException("Not implemented");
  }

  protected void loadModel() throws Exception {
    byte[] modelData = MLFunction.getModelStore().loadModel(modelName + "_data");
    model = ModelUtils.byteArrayToModel(modelData, "");
    isModelTrained = true;
  }
}


