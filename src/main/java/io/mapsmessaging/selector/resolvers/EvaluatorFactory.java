/*
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.mapsmessaging.selector.resolvers;

import io.mapsmessaging.selector.IdentifierResolver;
import java.util.Map;
import org.json.JSONObject;

public class EvaluatorFactory {

  @SuppressWarnings("unchecked")
  public static IdentifierResolver create(Object obj){
    if (obj instanceof IdentifierResolver) {
      return (IdentifierResolver) obj;
    }
    if (obj instanceof Map) {
      return new MapEvaluator((Map<String, Object>) obj);
    }
    if(obj instanceof JSONObject){
      return new JsonEvaluator((JSONObject) obj);
    }
    return new BeanEvaluator(obj);
  }

  private EvaluatorFactory(){}
}
