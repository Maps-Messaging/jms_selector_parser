package io.mapsmessaging.selector.operators.functions.ml.impl.functions;

import io.mapsmessaging.selector.operators.functions.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.ModelUtils;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import weka.core.Instance;
import weka.core.Instances;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class TensorFlowOperation extends AbstractMLModelOperation {
  private SavedModelBundle model;

  public TensorFlowOperation(String modelName, List<String> identity, long time, long samples) {
    super(modelName, identity, time, samples);
  }

  @Override
  protected void initializeSpecificModel() throws Exception {
    // Initialize TensorFlow model
  }

  protected void buildModel(Instances trainingData) throws Exception{}




  @Override
  protected double applyModel(Instance instance) throws Exception {
    // Extract feature values from the instance
    float[] features = new float[instance.numAttributes()];
    for (int i = 0; i < instance.numAttributes(); i++) {
      features[i] = (float) instance.value(i);
    }

    // Create a TensorFlow tensor from the extracted feature values
    try (Tensor<Float> inputTensor =
             Tensor.create(new long[]{1, features.length}, FloatBuffer.wrap(features));
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
  }

  protected void loadModel() throws Exception {
    byte[] modelData = MLFunction.getModelStore().loadModel(modelName + "_data");
    structure = ModelUtils.byteArrayToInstances(modelData);
    byte[] tfModelData = MLFunction.getModelStore().loadModel(modelName + "_tfmodel");
    model = ModelUtils.byteArrayToModel(tfModelData, "temp");
    isModelTrained = true;
  }
}


