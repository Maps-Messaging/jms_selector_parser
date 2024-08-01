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
import io.mapsmessaging.selector.operators.ComparisonOperator;
import io.mapsmessaging.selector.operators.FunctionOperator;
import io.mapsmessaging.selector.operators.Operation;
import io.mapsmessaging.selector.operators.comparison.GreaterOrEqualOperator;
import io.mapsmessaging.selector.operators.comparison.LessOrEqualOperator;

public class BetweenOperator extends FunctionOperator {

  private final Object lhs;
  private final ComparisonOperator bottomOperator;
  private final ComparisonOperator topOperator;

  public BetweenOperator(Object lhs, Object lowest, Object highest){
    if(lowest instanceof Operation){
      lowest = ((Operation)lowest).compile();
    }
    if(highest instanceof Operation){
      highest = ((Operation)highest).compile();
    }

    this.lhs = lhs;
    bottomOperator = new GreaterOrEqualOperator(lhs, lowest);
    topOperator = new LessOrEqualOperator(lhs, highest);
  }

  public Object compile(){
    bottomOperator.compile();
    if(lhs instanceof Number &&
        bottomOperator.getRHS() instanceof Number &&
        topOperator.getRHS() instanceof Number){
      boolean bottom  = (Boolean) bottomOperator.evaluate(lhs, bottomOperator.getRHS());
      boolean top  = (Boolean) topOperator.evaluate(lhs, topOperator.getRHS());
      return top && bottom;
    }
    return this;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    boolean bottom = ((Boolean)bottomOperator.evaluate(resolver));
    if(bottom) {
      return topOperator.evaluate(resolver);
    }
    return false;
  }

  @Override
  public String toString(){
    return "("+lhs.toString() +") BETWEEN ("+ bottomOperator.getRHS()+" AND "+topOperator.getRHS()+")";
  }

  @Override
  public boolean equals(Object test){
    if(test instanceof BetweenOperator){
      return (lhs.equals(((BetweenOperator) test).lhs) &&
          bottomOperator.equals(((BetweenOperator) test).bottomOperator) &&
          topOperator.equals(((BetweenOperator) test).topOperator));
    }
    return false;
  }

  @Override
  public int hashCode(){
    return lhs.hashCode() | bottomOperator.hashCode() ^ topOperator.hashCode();
  }

}
