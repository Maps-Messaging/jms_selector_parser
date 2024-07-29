package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.functions.ml.KMeansClusterOperation;
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

  private static double[] results = {0.057, 0.085, 0.057, 16.603};

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  @Test
  void testModel() throws ParseException {
    List<String> dataList = new ArrayList<>();
    dataList.add("0");
    dataList.add("1");
    KMeansClusterOperation kMeansOperation = new KMeansClusterOperation("value",dataList , 10000, trainingData.length);
    ArrayIdentifierResolver resolver = new ArrayIdentifierResolver(trainingData);
    // Train the model with the training data
    while(resolver.index< trainingData.length){
      kMeansOperation.evaluate(resolver);
      resolver.index++;
    }

    // Apply the model to new test data
    resolver = new ArrayIdentifierResolver(testData);
    while(resolver.index< testData.length){
      double distance = (double)  kMeansOperation.evaluate(resolver);
      Assertions.assertEquals( round(results[resolver.index], 3), round(distance, 3));
      resolver.index++;
    }
  }


  class ArrayIdentifierResolver implements IdentifierResolver {

    private double[][] data;
    protected int index =0;

    public ArrayIdentifierResolver(double[][] data) {
      this.data = data;
    }

    @Override
    public Object get(String key) {
      int idx = Integer.parseInt(key);
      return data[index][idx];
    }

    @Override
    public byte[] getOpaqueData() {
      return IdentifierResolver.super.getOpaqueData();
    }
  }


}
