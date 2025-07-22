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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.tensorflow;

import org.tensorflow.Tensor;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.proto.DataType;
import org.tensorflow.types.*;

import static org.tensorflow.proto.DataType.*;

public class TensorBuilder {

  public static Tensor createTensor(Object[] values, DataType dtype, Shape shape) {
    if (dtype.equals(DT_FLOAT)) {
      TFloat32 tensor = TFloat32.tensorOf(shape);
      for (int i = 0; i < values.length; i++) {
        tensor.setFloat(((Number) values[i]).floatValue(), 0, i);
      }
      return tensor;
    }

    if (dtype.equals(DT_INT32)) {
      TInt32 tensor = TInt32.tensorOf(shape);
      for (int i = 0; i < values.length; i++) {
        tensor.setInt(((Number) values[i]).intValue(), 0, i);
      }
      return tensor;
    }

    if (dtype.equals(DT_INT64)) {
      TInt64 tensor = TInt64.tensorOf(shape);
      for (int i = 0; i < values.length; i++) {
        tensor.setLong(((Number) values[i]).longValue(), 0, i);
      }
      return tensor;
    }

    if (dtype.equals(DT_BOOL)) {
      TBool tensor = TBool.tensorOf(shape);
      for (int i = 0; i < values.length; i++) {
        tensor.setBoolean((Boolean) values[i], 0, i);
      }
      return tensor;
    }

    throw new IllegalArgumentException("Unsupported tensor type: " + dtype.name());
  }
  private TensorBuilder() {}
}

