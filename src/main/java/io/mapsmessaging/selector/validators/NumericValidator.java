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

package io.mapsmessaging.selector.validators;

import io.mapsmessaging.selector.Identifier;
import io.mapsmessaging.selector.operators.ArithmeticOperator;
import io.mapsmessaging.selector.operators.arithmetic.NegateOperator;

public class NumericValidator implements Validator {

  private NumericValidator(){}

  public static boolean isValid(Object parameter) {
    return
        (parameter instanceof Identifier) ||  // This could return a number value from the resolver
        (parameter instanceof ArithmeticOperator) ||
        (parameter instanceof Number ) ||
        (parameter instanceof NegateOperator);
  }
}
