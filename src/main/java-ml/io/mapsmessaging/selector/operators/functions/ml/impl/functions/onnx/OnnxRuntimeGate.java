/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2026 ] MapsMessaging B.V.
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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.onnx;

import ai.onnxruntime.OrtEnvironment;

public class OnnxRuntimeGate {

  public static final boolean AVAILABLE;
  static {
    boolean ok;
    try {
      // Try init without keeping a ref; this loads the native lib
      OrtEnvironment.getEnvironment().close(); // no-op safe
      ok = true;
    } catch (Throwable t) {
      ok = false; // UnsatisfiedLinkError, missing deps, etc.
    }
    AVAILABLE = ok && !Boolean.getBoolean("maps.ml.onnx.disabled")
        && !"1".equals(System.getenv("MAPS_ML_ONNX_DISABLED"));
  }
  private OnnxRuntimeGate() {}
}
