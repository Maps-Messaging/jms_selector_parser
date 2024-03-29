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

package io.mapsmessaging.selector.operators.arithmetic;

import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.ArithmeticOperator;

public class SubtractOperator extends ArithmeticOperator {


  public SubtractOperator(Object lhs, Object rhs) throws ParseException {
    super( lhs, rhs);
  }

  @Override
  protected Number compute( double lhs, double rhs){
    return lhs - rhs;
  }

  @Override
  protected Number compute( double lhs, long rhs) {
    return lhs - rhs;
  }

  @Override
  protected Number compute( long lhs, double rhs) {
    return lhs - rhs;
  }

  @Override
  protected Number compute(long lhs, long rhs) {
    return lhs - rhs;
  }

  @Override
  public String toString(){
    return "("+lhs.toString() +") - ("+rhs.toString()+")";
  }

  @Override
  public boolean equals(Object test) {
    if (test instanceof SubtractOperator) {
      return (lhs.equals(((SubtractOperator) test).lhs) && rhs.equals(((SubtractOperator) test).rhs));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() <<4 ^ rhs.hashCode()>>4;
  }
}
