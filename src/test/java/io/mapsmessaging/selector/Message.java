package io.mapsmessaging.selector;

import java.util.Map;

public class Message implements IdentifierMutator {

  private Map<String, Object> map;
  private byte[] opaqueData;

  public Message() {

  }

  @Override
  public Object get(String key) {
    if (map != null) {
      return map.get(key);
    }
    return null;
  }

  @Override
  public Object remove(String key) {
    if (map != null) {
      return map.remove(key);
    }
    return null;
  }

  @Override
  public Object set(String key, Object value) {
    if (map != null) {
      return map.put(key, value);
    }
    return null;
  }

  @Override
  public byte[] getOpaqueData() {
    return opaqueData;
  }

  public void setOpaqueData(byte[] opaqueData) {
    this.opaqueData = opaqueData;
  }

  public Map<String, Object> getMap() {
    return map;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }
}
