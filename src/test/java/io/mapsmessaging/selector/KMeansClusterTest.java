package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import io.mapsmessaging.selector.operators.functions.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.KMeansClusterOperation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.List;

class KMeansClusterTest {

  private static double[][] trainingData = {
      {5.0, 5.0}, {6.0, 5.0}, {5.0, 6.0}, {6.0, 6.0}, {4.0, 4.0},
      {15.0, 15.0}, {16.0, 15.0}, {15.0, 16.0}, {16.0, 16.0}, {14.0, 14.0},
      {25.0, 25.0}, {26.0, 25.0}, {25.0, 26.0}, {26.0, 26.0}, {24.0, 24.0}
  };

  private static double[][] testData = {
      {6.0, 6.0}, {14.0, 14.0}, {26.0, 26.0}, {260.0, 260.0}
  };

  private static String[] results = {
      "K-means_clustering (modelName1, a0, a1) between 0.047 and 0.050",
      "K-means_clustering (modelName1, a0, a1) between 0.162 and 0.164",
      "K-means_clustering (modelName1, a0, a1) between 1.472 and 1.474",
      "K-means_clustering (modelName1, a0, a1) > 16"
  };


  @Test
  void testModel() throws ParseException {
    ParserExecutor executor = SelectorParser.compile("K-means_clustering ( modelName1 , a0 , a1) > 0 OR NOT model_exists(modelName1)");
    ArrayIdentifierResolver resolver = new ArrayIdentifierResolver(trainingData);
    // Train the model with the training data
    while(resolver.index< trainingData.length){
      Assertions.assertTrue(executor.evaluate(resolver));
      resolver.index++;
    }

    // Apply the model to new test data
    resolver = new ArrayIdentifierResolver(testData);
    while(resolver.index< testData.length){
      executor = SelectorParser.compile(results[resolver.index]);
      boolean result = executor.evaluate(resolver);
      Assertions.assertTrue(result);
      resolver.index++;
    }
  }

  @Test
  void testReloadModel() throws Exception {
    ParserExecutor executor = SelectorParser.compile("K-means_clustering ( model , a0 , a1) > 0");
    ArrayIdentifierResolver resolver = new ArrayIdentifierResolver(trainingData);
    // Train the model with the training data
    while(resolver.index< trainingData.length){
      executor.evaluate(resolver);
      resolver.index++;
    }


    Assertions.assertTrue(MLFunction.getModelStore().modelExists("model"));
    resolver = new ArrayIdentifierResolver(testData);
    while(resolver.index< testData.length){
      executor = SelectorParser.compile(results[resolver.index]);
      Assertions.assertTrue(executor.evaluate(resolver));
      resolver.index++;
    }
  }



  class ArrayIdentifierResolver implements IdentifierResolver {

    private final double[][] data;
    protected int index =0;

    public ArrayIdentifierResolver(double[][] data) {
      this.data = data;
    }

    @Override
    public Object get(String key) {
      key = key.substring(1);
      int idx = Integer.parseInt(key);
      return data[index][idx];
    }

    @Override
    public byte[] getOpaqueData() {
      return IdentifierResolver.super.getOpaqueData();
    }
  }


}
