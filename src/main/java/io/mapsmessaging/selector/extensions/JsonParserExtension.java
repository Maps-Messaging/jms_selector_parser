/*
 *    Copyright [ 2020 - 2023 ] [Matthew Buckton]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

package io.mapsmessaging.selector.extensions;

import io.mapsmessaging.selector.ParseException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.json.JSONArray;
import org.json.JSONObject;
import io.mapsmessaging.selector.IdentifierResolver;

public class JsonParserExtension implements ParserExtension {

  private final String[] keyPath;

  public JsonParserExtension(){
    keyPath = null;
  }

  public JsonParserExtension(List<String> arguments) throws ParseException {
    if(arguments.isEmpty()) throw new ParseException("Requires at least 1 argument");
    String key = arguments.get(0);
    if(key.contains(".")){
      var stringTokenizer = new StringTokenizer(key, ".");
      List<String> tmp = new ArrayList<>();
      while(stringTokenizer.hasMoreElements()){
        tmp.add(stringTokenizer.nextElement().toString());
      }
      var tmpPath = new String[tmp.size()];
      keyPath = tmp.toArray(tmpPath);
    }
    else{
      keyPath = new String[1];
      keyPath[0] = key;
    }
  }

  @Override
  public ParserExtension createInstance(List<String> arguments) throws ParseException {
    return new JsonParserExtension(arguments);
  }

  @Override
  public String getName(){
    return "json";
  }

  @Override
  public String getDescription() {
    return "Parses the byte[] as a JSON object to enable filtering via the JSON object";
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) {
    byte[] payload = resolver.getOpaqueData();
    if (payload != null && payload.length > 0) {
      var json = new JSONObject(new String(payload));
      if (!json.isEmpty()) {
        var located = locateObject(json, keyPath);
        return parseJSON(located);
      }
    }
    return null;
  }

  public Object locateObject(JSONObject json){
    Object t = locateObject(json, keyPath);
    if(t != null){
      t = parseJSON(t);
    }
    return t;
  }

  public Object locateObject(JSONObject json, String[] searchPath){
    if(keyPath != null){
      // Walk the JSON path first
      for(var x=0;x<searchPath.length;x++){
        var path = searchPath[x];
        var jsonObject = json.get(path);
        if(jsonObject instanceof JSONArray){
          var sub = new String[searchPath.length-(x +1)];
          System.arraycopy(searchPath, x+1, sub, 0, sub.length);
          return arrayLookup(json.getJSONArray(path), sub);
        }
        else if(jsonObject instanceof JSONObject){
          json = (JSONObject) jsonObject;
        }
        else{
          return jsonObject;
        }
      }
    }
    return null;
  }

  private Object arrayLookup(JSONArray array, String[] path){
    // We have an array, so the next element in the path must be an index ( ie number)
    var idx = Integer.parseInt(path[0]);
    Object lookup = array.get(idx);
    if(lookup instanceof JSONObject){
      var sub = new String[path.length-1];
      System.arraycopy(path, 1, sub, 0, sub.length);
      return locateObject( (JSONObject) lookup, sub);
    }
    else if(lookup instanceof JSONArray){
      var sub = new String[path.length-1];
      System.arraycopy(path, 1, sub, 0, sub.length);
      return arrayLookup( (JSONArray) lookup, sub);
    }
    return lookup;
  }

  private Object parseJSON(Object lookup){
    if (lookup instanceof String ||
        lookup instanceof Float ||
        lookup instanceof Double ||
        lookup instanceof Byte ||
        lookup instanceof Short ||
        lookup instanceof Integer ||
        lookup instanceof Long) {
      return lookup;
    }
    else if(lookup instanceof BigDecimal){
      return ((BigDecimal)lookup).doubleValue();
    }
    return null;
  }

  @Override
  public String toString(){
    var sb = new StringBuilder("JSON, '");
    for(var path:keyPath){
      sb.append(path).append("' ,");
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object test){
    if(test instanceof JsonParserExtension){
      var rhs = (JsonParserExtension)test;
      if(keyPath.length == rhs.keyPath.length){
        for(var x=0;x<keyPath.length;x++){
          if(!keyPath[x].equals(rhs.keyPath[x])){
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode(){
    long largeHash = 0;
    for(var path:keyPath){
      largeHash += path.hashCode();
    }
    return Math.toIntExact(largeHash);
  }

}
