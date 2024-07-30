package io.mapsmessaging.selector.operators.functions.ml.impl.store;

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

import java.io.*;

public class ModelUtils {

  public static byte[] instancesToByteArray(Instances instances) throws IOException {
    ArffSaver saver = new ArffSaver();
    saver.setInstances(instances);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    saver.setDestination(byteArrayOutputStream);
    saver.writeBatch();

    return byteArrayOutputStream.toByteArray();
  }

  public static Instances byteArrayToInstances(byte[] data) throws IOException {
    ArffLoader loader = new ArffLoader();
    loader.setSource(new ByteArrayInputStream(data));
    return loader.getDataSet();
  }

  private ModelUtils(){}
}
