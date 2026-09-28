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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnnxTensorBuilderTest {

  private final OrtEnvironment environment = OrtEnvironment.getEnvironment();

  @Test
  void createsFloatTensorsFromPrimitiveAndBoxedFeatures() throws Exception {
    for (Object features : new Object[]{
        new float[]{1.5f, -2.0f},
        new double[]{1.5, -2.0},
        new Number[]{1.5f, -2}
    }) {
      try (OnnxTensor tensor = OnnxTensorBuilder.createTensorFromFeatures(
          environment, OnnxJavaType.FLOAT, 2, features)) {
        assertArrayEquals(new float[]{1.5f, -2.0f}, ((float[][]) tensor.getValue())[0]);
      }
    }
  }

  @Test
  void createsDoubleTensorsFromPrimitiveAndBoxedFeatures() throws Exception {
    for (Object features : new Object[]{
        new double[]{1.5, -2.0},
        new Number[]{1.5, -2},
        new float[]{1.5f, -2.0f}
    }) {
      try (OnnxTensor tensor = OnnxTensorBuilder.createTensorFromFeatures(
          environment, OnnxJavaType.DOUBLE, 2, features)) {
        assertArrayEquals(new double[]{1.5, -2.0}, ((double[][]) tensor.getValue())[0]);
      }
    }
  }

  @Test
  void createsInt32AndInt64TensorsFromSupportedFeatures() throws Exception {
    for (Object features : new Object[]{new int[]{4, -7}, new Number[]{4L, -7.0}}) {
      try (OnnxTensor tensor = OnnxTensorBuilder.createTensorFromFeatures(
          environment, OnnxJavaType.INT32, 2, features)) {
        assertArrayEquals(new int[]{4, -7}, ((int[][]) tensor.getValue())[0]);
      }
    }

    for (Object features : new Object[]{
        new long[]{4L, -7L},
        new Number[]{4, -7L},
        new int[]{4, -7}
    }) {
      try (OnnxTensor tensor = OnnxTensorBuilder.createTensorFromFeatures(
          environment, OnnxJavaType.INT64, 2, features)) {
        assertArrayEquals(new long[]{4L, -7L}, ((long[][]) tensor.getValue())[0]);
      }
    }
  }

  @Test
  void createsBooleanTensorsFromPrimitiveAndBoxedFeatures() throws Exception {
    for (Object features : new Object[]{
        new boolean[]{true, false, false},
        new Boolean[]{true, false, null}
    }) {
      try (OnnxTensor tensor = OnnxTensorBuilder.createTensorFromFeatures(
          environment, OnnxJavaType.BOOL, 3, features)) {
        assertArrayEquals(new boolean[]{true, false, false}, ((boolean[][]) tensor.getValue())[0]);
      }
    }
  }

  @Test
  void rejectsIncorrectFeatureLengthsForEverySupportedType() {
    for (OnnxJavaType type : new OnnxJavaType[]{
        OnnxJavaType.FLOAT, OnnxJavaType.DOUBLE, OnnxJavaType.INT32,
        OnnxJavaType.INT64, OnnxJavaType.BOOL
    }) {
      Object features = switch (type) {
        case FLOAT -> new float[]{1};
        case DOUBLE -> new double[]{1};
        case INT32 -> new int[]{1};
        case INT64 -> new long[]{1};
        case BOOL -> new boolean[]{true};
        default -> throw new AssertionError(type);
      };
      IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
          () -> OnnxTensorBuilder.createTensorFromFeatures(environment, type, 2, features));
      assertTrue(failure.getMessage().contains("Feature length"));
    }
  }

  @Test
  void rejectsUnsupportedFeaturesAndTensorTypes() {
    for (OnnxJavaType type : new OnnxJavaType[]{
        OnnxJavaType.FLOAT, OnnxJavaType.DOUBLE, OnnxJavaType.INT32,
        OnnxJavaType.INT64, OnnxJavaType.BOOL
    }) {
      IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
          () -> OnnxTensorBuilder.createTensorFromFeatures(environment, type, 2, "not numeric"));
      assertTrue(failure.getMessage().contains("Cannot coerce"));
    }

    IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
        () -> OnnxTensorBuilder.createTensorFromFeatures(
            environment, OnnxJavaType.STRING, 1, new String[]{"text"}));
    assertTrue(failure.getMessage().contains("Unsupported ONNX Java type"));
  }
}
