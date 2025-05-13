/*
 *  Copyright [ 2020 - 2025 ] [Matthew Buckton]
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

import com.google.gson.JsonObject;
import io.mapsmessaging.selector.IdentifierResolver;
import java.util.Map;


public class EvaluatorFactory {

  @SuppressWarnings("unchecked")
  public static IdentifierResolver create(Object obj){
    if (obj instanceof IdentifierResolver) {
      return (IdentifierResolver) obj;
    }
    if (obj instanceof Map) {
      return new MapEvaluator((Map<String, Object>) obj);
    }
    if(obj instanceof JsonObject){
      return new JsonEvaluator((JsonObject) obj);
    }
    return new BeanEvaluator(obj);
  }

  private EvaluatorFactory(){}
}
