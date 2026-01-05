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


import ai.onnxruntime.OnnxJavaType;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import lombok.extern.slf4j.Slf4j;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

/**
 * Mirrors your TensorBuilder.
 */
@Slf4j
class OnnxTensorBuilder {

  public static OnnxTensor createTensorFromFeatures(final OrtEnvironment environment,
                                                    final OnnxJavaType onnxJavaType,
                                                    final long featureCount,
                                                    final Object features) throws OrtException {

    if (onnxJavaType == OnnxJavaType.FLOAT) {
      FloatBuffer buf = FloatBuffer.wrap(coerceToFloatArray(features, featureCount));
      return OnnxTensor.createTensor(environment, buf, new long[]{1L, featureCount});
    }

    if (onnxJavaType == OnnxJavaType.DOUBLE) {
      DoubleBuffer buf = DoubleBuffer.wrap(coerceToDoubleArray(features, featureCount));
      return OnnxTensor.createTensor(environment, buf, new long[]{1L, featureCount});
    }

    if (onnxJavaType == OnnxJavaType.INT32) {
      IntBuffer buf = IntBuffer.wrap(coerceToIntArray(features, featureCount));
      return OnnxTensor.createTensor(environment, buf, new long[]{1L, featureCount});
    }

    if (onnxJavaType == OnnxJavaType.INT64) {
      LongBuffer buf = LongBuffer.wrap(coerceToLongArray(features, featureCount));
      return OnnxTensor.createTensor(environment, buf, new long[]{1L, featureCount});
    }

    if (onnxJavaType == OnnxJavaType.BOOL) {
      boolean[] v = coerceToBooleanArray(features, featureCount);
      boolean[][] v2d = new boolean[1][(int) featureCount];
      System.arraycopy(v, 0, v2d[0], 0, v.length);
      return OnnxTensor.createTensor(environment, v2d);
    }

    // Strings and other types can be added as needed.
    throw new IllegalArgumentException("Unsupported ONNX Java type: " + onnxJavaType);
  }

  private static float[] coerceToFloatArray(final Object src, final long expected) {
    if (src instanceof float[]arr) {
      validateLength(arr.length, expected);
      return arr;
    }
    if (src instanceof double[]in) {
      validateLength(in.length, expected);
      float[] out = new float[in.length];
      for (int i = 0; i < in.length; i++) {
        out[i] = (float) in[i];
      }
      return out;
    }
    if (src instanceof Number[]in) {
      validateLength(in.length, expected);
      float[] out = new float[in.length];
      for (int i = 0; i < in.length; i++) {
        out[i] = in[i].floatValue();
      }
      return out;
    }
    throw new IllegalArgumentException("Cannot coerce features to float[]");
  }

  private static double[] coerceToDoubleArray(final Object src, final long expected) {
    if (src instanceof double[]arr) {
      validateLength(arr.length, expected);
      return arr;
    }
    if (src instanceof Number[] in) {
      validateLength(in.length, expected);
      double[] out = new double[in.length];
      for (int i = 0; i < in.length; i++) {
        out[i] = in[i].doubleValue();
      }
      return out;
    }
    if (src instanceof float[] in) {
      validateLength(in.length, expected);
      double[] out = new double[in.length];
      for (int i = 0; i < in.length; i++) {
        out[i] = in[i];
      }
      return out;
    }
    throw new IllegalArgumentException("Cannot coerce features to double[]");
  }

  private static int[] coerceToIntArray(final Object src, final long expected) {
    if (src instanceof int[] arr) {
      validateLength(arr.length, expected);
      return arr;
    }
    if (src instanceof Number[] in) {
      validateLength(in.length, expected);
      int[] out = new int[in.length];
      for (int i = 0; i < in.length; i++) {
        out[i] = in[i].intValue();
      }
      return out;
    }
    throw new IllegalArgumentException("Cannot coerce features to int[]");
  }

  private static long[] coerceToLongArray(final Object src, final long expected) {
    if (src instanceof long[] arr) {
      validateLength(arr.length, expected);
      return arr;
    }
    if (src instanceof Number[] in) {
      validateLength(in.length, expected);
      long[] out = new long[in.length];
      for (int i = 0; i < in.length; i++) {
        out[i] = in[i].longValue();
      }
      return out;
    }
    if (src instanceof int[] in) {
      validateLength(in.length, expected);
      long[] out = new long[in.length];
      for (int i = 0; i < in.length; i++) {
        out[i] = in[i];
      }
      return out;
    }
    throw new IllegalArgumentException("Cannot coerce features to long[]");
  }

  private static boolean[] coerceToBooleanArray(final Object src, final long expected) {
    if (src instanceof boolean[] arr) {
      validateLength(arr.length, expected);
      return arr;
    }
    if (src instanceof Boolean[] in) {
      validateLength(in.length, expected);
      boolean[] out = new boolean[in.length];
      for (int i = 0; i < in.length; i++) {
        out[i] = Boolean.TRUE.equals(in[i]);
      }
      return out;
    }
    throw new IllegalArgumentException("Cannot coerce features to boolean[]");
  }

  private static void validateLength(final int length, final long expected) {
    if (expected >= 0 && length != expected) {
      throw new IllegalArgumentException("Feature length " + length + " does not match expected " + expected);
    }
  }

  private OnnxTensorBuilder(){}
}