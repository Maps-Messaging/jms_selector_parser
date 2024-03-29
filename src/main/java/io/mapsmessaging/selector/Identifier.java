/*
 *
 *   Copyright [ 2020 - 2023 ] [Matthew Buckton]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.Operation;
import lombok.Getter;

public class Identifier extends Operation {

  @Getter
  private final String key;

  public Identifier(String key){
    this.key = key;
  }

  public Object evaluate(IdentifierResolver resolver){
    if(resolver == null){
      return null;
    }
    Object response = resolver.get(key);
    if (response != null) {
      if(response instanceof Number){
        if(response instanceof Double){
          return ((Number)response).doubleValue();
        }
        if(response instanceof Float){
          return ((Number)response).floatValue();
        }
        return ((Number)response).longValue();
      }
      return response;
    }
    return null;
  }

  @Override
  public boolean equals(Object rhs){
    if(rhs instanceof Identifier){
      return key.equals( ((Identifier)rhs).key);
    }
    return false;
  }

  @Override
  public int hashCode(){
    return key.hashCode();
  }

  @Override
  public Object compile() {
    return this;
  }

  public String toString(){
    return "<IDENTIFIER>["+key+"]";
  }
}
