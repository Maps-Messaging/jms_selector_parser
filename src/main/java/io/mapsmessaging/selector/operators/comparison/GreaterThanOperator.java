/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2025 ] MapsMessaging B.V.
 *
 *  Licensed under the Apache License, Version 2.0 with the Commons Clause
 *  (the "License"); you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      https://commonsclause.com/
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.mapsmessaging.selector.operators.comparison;

import io.mapsmessaging.selector.operators.ComparisonOperator;

public class GreaterThanOperator extends ComparisonOperator {

  public GreaterThanOperator(Object lhs, Object rhs) {
    super(lhs, rhs);
  }

  @Override
  protected Boolean compute(double lhs, double rhs) {
    return lhs > rhs;
  }

  @Override
  protected Boolean compute(double lhs, long rhs) {
    return lhs > rhs;
  }

  @Override
  protected Boolean compute(long lhs, double rhs) {
    return lhs > rhs;
  }

  @Override
  protected Boolean compute(long lhs, long rhs) {
    return lhs > rhs;
  }

  @Override
  public String toString() {
    return "(" + lhs.toString() + ") > (" + rhs.toString() + ")";
  }

  @Override
  public boolean equals(Object test) {
    if (test instanceof GreaterThanOperator operator) {
      return (lhs.equals(operator.lhs) && rhs.equals(operator.rhs));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() ^ rhs.hashCode();
  }
}
