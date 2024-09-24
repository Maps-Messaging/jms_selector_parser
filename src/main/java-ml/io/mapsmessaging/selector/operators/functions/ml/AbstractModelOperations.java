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

package io.mapsmessaging.selector.operators.functions.ml;import io.mapsmessaging.selector.operators.Operation;
import java.util.List;

public abstract class AbstractModelOperations extends Operation {
  protected final List<String> identity;
  protected final String modelName;
  protected boolean isModelTrained;

  protected AbstractModelOperations(String modelName, List<String> identity) {
    this.identity = identity;
    this.modelName = modelName;
    isModelTrained = false;
  }

  @Override
  public Object compile() {
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(modelName);
    for (String s : identity) {
      sb.append(", ").append(s);
    }
    return sb.toString();
  }
}
