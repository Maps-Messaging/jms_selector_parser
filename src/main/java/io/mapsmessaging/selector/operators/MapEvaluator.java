package io.mapsmessaging.selector.operators;

import io.mapsmessaging.selector.IdentifierResolver;

import java.util.Map;

class MapEvaluator implements IdentifierResolver {

  private final Map<String, Object> map;

  public MapEvaluator(Map<String, Object> map){
    this.map= map;
  }

  @Override
  public Object get(String key) {
    return map.get(key);
  }
}