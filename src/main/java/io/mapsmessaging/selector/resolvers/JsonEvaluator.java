/*
 *
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *  Copyright [ 2024 - 2025.  ] [Maps Messaging B.V.]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package io.mapsmessaging.selector.resolvers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.mapsmessaging.selector.IdentifierMutator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class JsonEvaluator extends IdentifierMutator {

  private final JsonObject jsonObject;

  public JsonEvaluator(JsonObject jsonObject){
    this.jsonObject = jsonObject;
  }


  @Override
  public Object get(String key) {
    String[] keyPath = getKeyPath(key);
    Object lookup = locateObject(jsonObject, keyPath);
    return parseJSON(lookup);
  }

  @Override
  public Object remove(String key) {
    return get(key);
  }

  @Override
  public Object set(String key, Object value) {
    return false;
  }

  private String[] getKeyPath(String key){
    String[] keyPath;
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
    return keyPath;
  }

  private static  Object locateObject(JsonObject json, String[] searchPath){
    if(searchPath != null){
      // Walk the JSON path first
      for(var x=0;x<searchPath.length;x++){
        var path = searchPath[x];
        var jsonLookup = json.get(path);
        if(jsonLookup instanceof JsonArray){
          var sub = new String[searchPath.length-(x +1)];
          System.arraycopy(searchPath, x+1, sub, 0, sub.length);
          return arrayLookup(json.getAsJsonArray(path), sub);
        }
        else if(jsonLookup instanceof JsonObject){
          json = (JsonObject) jsonLookup;
        }
        else{
          return jsonLookup;
        }
      }
    }
    return null;
  }

  private static Object arrayLookup(JsonArray array, String[] path){
    // We have an array, so the next element in the path must be an index ( ie number)
    var idx = Integer.parseInt(path[0]);
    Object lookup = array.get(idx);
    if(lookup instanceof JsonObject){
      var sub = new String[path.length-1];
      System.arraycopy(path, 1, sub, 0, sub.length);
      return locateObject( (JsonObject) lookup, sub);
    }
    else if(lookup instanceof JsonArray){
      var sub = new String[path.length-1];
      System.arraycopy(path, 1, sub, 0, sub.length);
      return arrayLookup( (JsonArray) lookup, sub);
    }
    return lookup;
  }

  private static Object parseJSON(Object lookup){
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

}