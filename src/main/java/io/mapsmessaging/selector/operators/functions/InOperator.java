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

package io.mapsmessaging.selector.operators.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.FunctionOperator;
import java.util.Set;
import java.util.TreeSet;

public class InOperator extends FunctionOperator {

  private final Object lhs;
  private final Set<String> set;

  public InOperator(Object lhs, Set<String> entries){
    this.lhs = lhs;
    this.set = new TreeSet<>(entries);
  }

  public Object compile(){
    if(lhs instanceof String){
      return evaluate((String)lhs);
    }
    return this;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object lookup = evaluate(lhs, resolver);
    if(lookup != null) {
      if(lookup instanceof String){
        return evaluate((String)lookup);
      }
      return evaluate(lookup.toString());
    }
    return false;
  }

  private Object evaluate(String lookup){
    if(lookup != null) {
      return set.contains(lookup);
    }
    return false;
  }

  public String toString(){
    var tmp = new StringBuilder("(" + lhs.toString() + ") IN (");
    for(String check:set){
      tmp.append(check).append(",");
    }

    tmp.append(")");
    return tmp.toString();
  }

  @Override
  public boolean equals(Object test){
    if(test instanceof InOperator){
      return (lhs.equals(((InOperator) test).lhs) &&
          set.equals(((InOperator) test).set));
    }
    return false;
  }

  @Override
  public int hashCode(){
    return lhs.hashCode() ^ set.hashCode();
  }

}
