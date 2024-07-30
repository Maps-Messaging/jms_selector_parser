package io.mapsmessaging.selector.operators.functions.ml.impl.store;

import io.mapsmessaging.selector.operators.functions.ml.ModelStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapModelStore implements ModelStore {
  private final Map<String, byte[]> modelStore;


  public MapModelStore() {
    modelStore = new ConcurrentHashMap<>();
  }

  @Override
  public void saveModel(String modelId, byte[] modelData) throws Exception {
    modelStore.put(modelId, modelData);
  }

  @Override
  public byte[] loadModel(String modelId) throws Exception {
    return modelStore.get(modelId);
  }

  @Override
  public boolean modelExists(String modelId) throws Exception {
    return modelStore.containsKey(modelId);
  }
}
