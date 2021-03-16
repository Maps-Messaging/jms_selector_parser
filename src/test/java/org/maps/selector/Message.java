package org.maps.selector;

import java.util.Map;
import org.maps.selector.operators.IdentifierResolver;

public class Message implements IdentifierResolver {

  private Map<String, Object> map;
  private byte[] opaqueData;

  public Message(){

  }

  @Override
  public Object get(String key) {
    if (map != null) {
      return map.get(key);
    }
    return null;
  }

  @Override
  public byte[] getOpaqueData() {
    return opaqueData;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }

  public void setOpaqueData(byte[] opaqueData) {
    this.opaqueData = opaqueData;
  }

  public Map<String, Object> getMap() {
    return map;
  }
}
