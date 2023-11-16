package io.mapsmessaging.selector.resolvers;

import org.json.JSONObject;

public class JsonEvaluator extends MapEvaluator {

  public JsonEvaluator(JSONObject bean){
    super(bean.toMap());
  }
}