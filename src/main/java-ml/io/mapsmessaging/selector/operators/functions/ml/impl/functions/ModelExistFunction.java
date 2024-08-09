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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.Operation;
import io.mapsmessaging.selector.operators.functions.ml.MLFunction;

public class ModelExistFunction extends Operation {

  private final String modelName;

  public ModelExistFunction(String modelName) {
    this.modelName = modelName;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    try {
      return MLFunction.getModelStore().modelExists(modelName);
    } catch (Exception e) {
      ParseException ex = new ParseException(e.getMessage());
      ex.initCause(e);
      throw ex;
    }
  }

  public String toString() {
    return "model_exist(" + modelName + ")";
  }

  @Override
  public Object compile() {
    return this;
  }
}
