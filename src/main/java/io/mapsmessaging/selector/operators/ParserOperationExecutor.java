/*
 *    Copyright [ 2020 - 2021 ] [Matthew Buckton]
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

package io.mapsmessaging.selector.operators;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import java.util.Map;

public class ParserOperationExecutor implements ParserExecutor {

  private final Operation parser;

  public ParserOperationExecutor(Operation parser)  {
    this.parser = parser;
  }

  public boolean evaluate(IdentifierResolver resolver){
    try {
      Object result = parser.evaluate(resolver);
      if(result instanceof Boolean){
        return (Boolean)result;
      }
    } catch (ParseException e) {
      // Log this exception
    }
    return false;
  }

  @Override
  public boolean evaluate(Map<String, Object> map) {
    return evaluate(new MapResolver(map));
  }

  @Override
  public String toString(){
    return parser.toString();
  }

  @Override
  public boolean equals(Object rhs){
    if(rhs instanceof ParserOperationExecutor){
      return parser.equals(((ParserOperationExecutor) rhs).parser);
    }
    return false;
  }

  @Override
  public int hashCode(){
    return parser.hashCode();
  }

  class MapResolver implements  IdentifierResolver{

    private final Map<String, Object> map;

    public MapResolver(Map<String, Object> map){
      this.map= map;
    }

    @Override
    public Object get(String key) {
      return map.get(key);
    }
  }
}
