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
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.proto.DataType;
import org.tensorflow.types.*;

import java.nio.charset.StandardCharsets;

public final class TensorBuilder {

  public static Tensor createTensor(Object values, DataType dtype, Shape shape) {
    if (values == null) throw new IllegalArgumentException("Values cannot be null");

    return switch (dtype) {
      case DT_FLOAT -> createFloatTensor(values, shape);
      case DT_DOUBLE -> createDoubleTensor(values, shape);
      case DT_INT32 -> createInt32Tensor(values, shape);
      case DT_INT64 -> createInt64Tensor(values, shape);
      case DT_BOOL -> createBoolTensor(values, shape);
      case DT_STRING -> createStringTensor(values, shape);
      default -> throw new IllegalArgumentException("Unsupported tensor type: " + dtype.name());
    };
  }

  private static Tensor createFloatTensor(Object values, Shape shape) {
    TFloat32 tensor = TFloat32.tensorOf(shape);


    if (values instanceof float[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setFloat(arr[i], i);
    } else if (values instanceof double[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setFloat((float) arr[i], i);
    } else if (values instanceof Number[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setFloat(arr[i].floatValue(), i);
    } else {
      throw new IllegalArgumentException("Unsupported float tensor source: " + values.getClass());
    }

    return tensor;
  }

  private static Tensor createInt32Tensor(Object values, Shape shape) {
    TInt32 tensor = TInt32.tensorOf(shape);

    if (values instanceof int[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setInt(arr[i], i);
    } else if (values instanceof short[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setInt(arr[i], i);
    } else if (values instanceof Number[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setInt(arr[i].intValue(), i);
    } else {
      throw new IllegalArgumentException("Unsupported int32 tensor source: " + values.getClass());
    }

    return tensor;
  }

  private static Tensor createInt64Tensor(Object values, Shape shape) {
    TInt64 tensor = TInt64.tensorOf(shape);

    if (values instanceof long[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setLong(arr[i], i);
    } else if (values instanceof Number[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setLong(arr[i].longValue(), i);
    } else {
      throw new IllegalArgumentException("Unsupported int64 tensor source: " + values.getClass());
    }

    return tensor;
  }

  private static Tensor createBoolTensor(Object values, Shape shape) {
    TBool tensor = TBool.tensorOf(shape);

    if (values instanceof boolean[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setBoolean(arr[i], i);
    } else if (values instanceof Boolean[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setBoolean(arr[i], i);
    } else {
      throw new IllegalArgumentException("Unsupported bool tensor source: " + values.getClass());
    }

    return tensor;
  }

  private static Tensor createDoubleTensor(Object values, Shape shape) {
    TFloat64 tensor = TFloat64.tensorOf(shape);

    if (values instanceof double[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setDouble(arr[i], i);
    } else if (values instanceof float[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setDouble(arr[i], i);
    } else if (values instanceof Number[] arr) {
      for (int i = 0; i < arr.length; i++) tensor.setDouble(arr[i].doubleValue(), i);
    } else {
      throw new IllegalArgumentException("Unsupported double tensor source: " + values.getClass());
    }

    return tensor;
  }


  private static Tensor createStringTensor(Object values, Shape shape) {
    var strings = NdArrays.ofObjects(String.class, shape);
    if (values instanceof String[] arr) {
      for (int i = 0; i < arr.length; i++) strings.setObject(arr[i], i);
    } else if (values instanceof byte[][] arr) {
      for (int i = 0; i < arr.length; i++) {
        strings.setObject(new String(arr[i], StandardCharsets.UTF_8), i);
      }
    } else {
      throw new IllegalArgumentException("Unsupported string tensor source: " + values.getClass());
    }
    return TString.tensorOf(strings);
  }


  private TensorBuilder() {}
}
