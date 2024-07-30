package io.mapsmessaging.selector.operators.functions.ml;

public interface ModelStore {
  void saveModel(String modelId, byte[] modelData) throws Exception;

  byte[] loadModel(String modelId) throws Exception;

  boolean modelExists(String modelId) throws Exception;
}
