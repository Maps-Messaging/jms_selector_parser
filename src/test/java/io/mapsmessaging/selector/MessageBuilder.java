package io.mapsmessaging.selector;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageBuilder {

  private final Message message;

  public MessageBuilder(){
    message = new Message();
  }

  public IdentifierResolver build() {
    return message;
  }

  public void setOpaqueData(byte[] bytes) {
    message.setOpaqueData(bytes);
  }

  public void setDataMap(Map<String, Object> map) {
    message.setMap(map);
  }

  public Map<String, Object> getDataMap() {
    Map<String, Object> map = message.getMap();
    if(map == null){
      map = new LinkedHashMap<>();
      message.setMap(map);
    }
    return map;
  }
}
