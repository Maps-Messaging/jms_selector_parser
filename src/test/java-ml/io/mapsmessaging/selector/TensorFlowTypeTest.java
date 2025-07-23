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

package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.functions.ml.impl.functions.tensorflow.TensorBuilder;
import org.junit.jupiter.api.Test;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.proto.DataType;
import org.tensorflow.types.*;

import static org.junit.jupiter.api.Assertions.*;

class TensorFlowTypeTest {
  @Test
  void testCreateFloatTensor() {
    float[] data = {1.1f, 2.2f};
    TFloat32 tensor = (TFloat32) TensorBuilder.createTensor(data, DataType.DT_FLOAT, Shape.of(1,2));
    assertEquals(1.1f, tensor.getFloat(0,0));
    assertEquals(2.2f, tensor.getFloat(0,1));
  }

  @Test
  void testCreateDoubleTensor() {
    double[] data = {1.0, 2.0};
    TFloat64 tensor = (TFloat64) TensorBuilder.createTensor(data, DataType.DT_DOUBLE, Shape.of(1,2));
    assertEquals(1.0, tensor.getDouble(0,0));
    assertEquals(2.0, tensor.getDouble(0,1));
  }

  @Test
  void testCreateInt32Tensor() {
    int[] data = {1, 2};
    TInt32 tensor = (TInt32) TensorBuilder.createTensor(data, DataType.DT_INT32, Shape.of(1,2));
    assertEquals(1, tensor.getInt(0,0));
    assertEquals(2, tensor.getInt(0,1));
  }

  @Test
  void testCreateInt64Tensor() {
    long[] data = {1L, 2L};
    TInt64 tensor = (TInt64) TensorBuilder.createTensor(data, DataType.DT_INT64, Shape.of(1,2));
    assertEquals(1L, tensor.getLong(0,0));
    assertEquals(2L, tensor.getLong(0,1));
  }

  @Test
  void testCreateBoolTensor() {
    boolean[] data = {true, false};
    TBool tensor = (TBool) TensorBuilder.createTensor(data, DataType.DT_BOOL, Shape.of(1,2));
    assertTrue(tensor.getBoolean(0,0));
    assertFalse(tensor.getBoolean(0,1));
  }

  @Test
  void testCreateStringTensor() {
    String[] data = {"one", "two"};
    TString tensor = (TString) TensorBuilder.createTensor(data, DataType.DT_STRING, Shape.of(1,2));
    assertEquals("one", tensor.getObject(0,0));
    assertEquals("two", tensor.getObject(0,1));
  }

  @Test
  void testStringTensorFromBytes() {
    byte[][] data = {"one".getBytes(), "two".getBytes()};
    TString tensor = (TString) TensorBuilder.createTensor(data, DataType.DT_STRING, Shape.of(1,2));
    assertEquals("one", tensor.getObject(0,0));
    assertEquals("two", tensor.getObject(0,1));
  }

  @Test
  void testCreateFloatTensorFromDoubleArray() {
    double[] data = {1.1, 2.2};
    TFloat32 tensor = (TFloat32) TensorBuilder.createTensor(data, DataType.DT_FLOAT, Shape.of(1,2));
    assertEquals(1.1f, tensor.getFloat(0,0));
    assertEquals(2.2f, tensor.getFloat(0,1));
  }

  @Test
  void testCreateFloatTensorFromNumberArrayWithLongs() {
    Number[] data = {1L, 2L};
    TFloat32 tensor = (TFloat32) TensorBuilder.createTensor(data, DataType.DT_FLOAT, Shape.of(1,2));
    assertEquals(1.0f, tensor.getFloat(0,0));
    assertEquals(2.0f, tensor.getFloat(0,1));
  }

  @Test
  void testCreateFloatTensorFromNumberArrayWithInts() {
    Number[] data = {1, 2};
    TFloat32 tensor = (TFloat32) TensorBuilder.createTensor(data, DataType.DT_FLOAT, Shape.of(1,2));
    assertEquals(1.0f, tensor.getFloat(0,0));
    assertEquals(2.0f, tensor.getFloat(0,1));
  }

  @Test
  void testCreateFloatTensorFromNumberArrayWithShorts() {
    Number[] data = {(short) 3, (short) 4};
    TFloat32 tensor = (TFloat32) TensorBuilder.createTensor(data, DataType.DT_FLOAT, Shape.of(1,2));
    assertEquals(3.0f, tensor.getFloat(0,0));
    assertEquals(4.0f, tensor.getFloat(0,1));
  }

  @Test
  void testCreateDoubleTensorFromDoubleArray() {
    double[] data = {1.0, 2.0};
    TFloat64 tensor = (TFloat64) TensorBuilder.createTensor(data, DataType.DT_DOUBLE, Shape.of(1,2));
    assertEquals(1.0, tensor.getDouble(0,0));
    assertEquals(2.0, tensor.getDouble(0,1));
  }

  @Test
  void testCreateDoubleTensorFromFloatArray() {
    float[] data = {1.1f, 2.2f};
    TFloat64 tensor = (TFloat64) TensorBuilder.createTensor(data, DataType.DT_DOUBLE, Shape.of(1,2));
    assertEquals(1.1, tensor.getDouble(0,0), 0.0001);
    assertEquals(2.2, tensor.getDouble(0,1), 0.0001);
  }

  @Test
  void testCreateDoubleTensorFromNumberArrayWithInts() {
    Number[] data = {1, 2};
    TFloat64 tensor = (TFloat64) TensorBuilder.createTensor(data, DataType.DT_DOUBLE, Shape.of(1,2));
    assertEquals(1.0, tensor.getDouble(0,0));
    assertEquals(2.0, tensor.getDouble(0,1));
  }

  @Test
  void testCreateDoubleTensorFromNumberArrayWithLongs() {
    Number[] data = {1L, 2L};
    TFloat64 tensor = (TFloat64) TensorBuilder.createTensor(data, DataType.DT_DOUBLE, Shape.of(1,2));
    assertEquals(1.0, tensor.getDouble(0,0));
    assertEquals(2.0, tensor.getDouble(0,1));
  }

  @Test
  void testCreateDoubleTensorFromNumberArrayWithFloats() {
    Number[] data = {1.1f, 2.2f};
    TFloat64 tensor = (TFloat64) TensorBuilder.createTensor(data, DataType.DT_DOUBLE, Shape.of(1,2));
    assertEquals(1.1, tensor.getDouble(0,0), 0.0001);
    assertEquals(2.2, tensor.getDouble(0,1), 0.0001);
  }


  @Test
  void testCreateInt64TensorFromNumberArrayWithInts() {
    Number[] data = {1, 2};
    TInt64 tensor = (TInt64) TensorBuilder.createTensor(data, DataType.DT_INT64, Shape.of(1,2));
    assertEquals(1L, tensor.getLong(0,0));
    assertEquals(2L, tensor.getLong(0,1));
  }

  @Test
  void testCreateInt64TensorFromNumberArrayWithFloats() {
    Number[] data = {1.0f, 2.0f};
    TInt64 tensor = (TInt64) TensorBuilder.createTensor(data, DataType.DT_INT64, Shape.of(1,2));
    assertEquals(1L, tensor.getLong(0,0));
    assertEquals(2L, tensor.getLong(0,1));
  }

  @Test
  void testCreateInt64TensorFromNumberArrayWithDoubles() {
    Number[] data = {1.0, 2.0};
    TInt64 tensor = (TInt64) TensorBuilder.createTensor(data, DataType.DT_INT64, Shape.of(1,2));
    assertEquals(1L, tensor.getLong(0,0));
    assertEquals(2L, tensor.getLong(0,1));
  }

  @Test
  void testCreateInt32TensorFromShortArray() {
    short[] data = {(short) 1, (short) 2};
    TInt32 tensor = (TInt32) TensorBuilder.createTensor(data, DataType.DT_INT32, Shape.of(1,2));
    assertEquals(1, tensor.getInt(0,0));
    assertEquals(2, tensor.getInt(0,1));
  }

  @Test
  void testCreateInt32TensorFromNumberArrayWithShorts() {
    Number[] data = {(short) 3, (short) 4};
    TInt32 tensor = (TInt32) TensorBuilder.createTensor(data, DataType.DT_INT32, Shape.of(1,2));
    assertEquals(3, tensor.getInt(0,0));
    assertEquals(4, tensor.getInt(0,1));
  }

  @Test
  void testCreateInt32TensorFromNumberArrayWithLongs() {
    Number[] data = {10L, 20L};
    TInt32 tensor = (TInt32) TensorBuilder.createTensor(data, DataType.DT_INT32, Shape.of(1,2));
    assertEquals(10, tensor.getInt(0,0));
    assertEquals(20, tensor.getInt(0,1));
  }

  @Test
  void testCreateInt32TensorFromNumberArrayWithDoubles() {
    Number[] data = {1.0, 2.0};
    TInt32 tensor = (TInt32) TensorBuilder.createTensor(data, DataType.DT_INT32, Shape.of(1,2));
    assertEquals(1, tensor.getInt(0,0));
    assertEquals(2, tensor.getInt(0,1));
  }

  @Test
  void testCreateBoolTensorFromBoxedBooleanArray() {
    Boolean[] data = {Boolean.TRUE, Boolean.FALSE};
    TBool tensor = (TBool) TensorBuilder.createTensor(data, DataType.DT_BOOL, Shape.of(1,2));
    assertTrue(tensor.getBoolean(0,0));
    assertFalse(tensor.getBoolean(0,1));
  }

  @Test
  void testCreateBoolTensorFromStringArray() {
    String[] data = {"true", "FALSE"};
    TBool tensor = (TBool) TensorBuilder.createTensor(data, DataType.DT_BOOL, Shape.of(1,2));
    assertTrue(tensor.getBoolean(0,0));
    assertFalse(tensor.getBoolean(0,1));
  }

  @Test
  void testCreateFloatTensorFromStringArray() {
    String[] data = {"1.0", "2.5"};
    TFloat32 tensor = (TFloat32) TensorBuilder.createTensor(data, DataType.DT_FLOAT, Shape.of(1,2));
    assertEquals(1.0f, tensor.getFloat(0,0));
    assertEquals(2.5f, tensor.getFloat(0,1));
  }

  @Test
  void testCreateDoubleTensorFromStringArray() {
    String[] data = {"3.14", "2.71"};
    TFloat64 tensor = (TFloat64) TensorBuilder.createTensor(data, DataType.DT_DOUBLE, Shape.of(1,2));
    assertEquals(3.14, tensor.getDouble(0,0));
    assertEquals(2.71, tensor.getDouble(0,1));
  }

  @Test
  void testCreateInt32TensorFromStringArray() {
    String[] data = {"10", "20"};
    TInt32 tensor = (TInt32) TensorBuilder.createTensor(data, DataType.DT_INT32, Shape.of(1,2));
    assertEquals(10, tensor.getInt(0,0));
    assertEquals(20, tensor.getInt(0,1));
  }

  @Test
  void testCreateInt64TensorFromStringArray() {
    String[] data = {"10000000000", "20000000000"};
    TInt64 tensor = (TInt64) TensorBuilder.createTensor(data, DataType.DT_INT64, Shape.of(1,2));
    assertEquals(10000000000L, tensor.getLong(0,0));
    assertEquals(20000000000L, tensor.getLong(0,1));
  }

  @Test
  void testCreateInt64TensorFromByteArray() {
    byte[] data = {5, 10};
    TInt64 tensor = (TInt64) TensorBuilder.createTensor(data, DataType.DT_INT64, Shape.of(1,2));
    assertEquals(5L, tensor.getLong(0,0));
    assertEquals(10L, tensor.getLong(0,1));
  }

  @Test
  void testCreateBoolTensorFromByteArray() {
    byte[] data = {1, 0};
    TBool tensor = (TBool) TensorBuilder.createTensor(data, DataType.DT_BOOL, Shape.of(1,2));
    assertTrue(tensor.getBoolean(0,0));
    assertFalse(tensor.getBoolean(0,1));
  }

  @Test
  void testCreateBoolTensorFromBoxedByteArray() {
    Byte[] data = {1, 0};
    TBool tensor = (TBool) TensorBuilder.createTensor(data, DataType.DT_BOOL, Shape.of(1,2));
    assertTrue(tensor.getBoolean(0,0));
    assertFalse(tensor.getBoolean(0,1));
  }

  @Test
  void testCreateStringTensorFromObjectArray() {
    Object[] data = {123, true, 3.14f};
    TString tensor = (TString) TensorBuilder.createTensor(data, DataType.DT_STRING, Shape.of(1,3));
    assertEquals("123", tensor.getObject(0,0));
    assertEquals("true", tensor.getObject(0,1));
    assertEquals("3.14", tensor.getObject(0,2));
  }

  @Test
  void testUnsupportedTypeThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(new Object(), DataType.DT_FLOAT, Shape.of(1,1)));
  }

  @Test
  void testNullValuesThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(null, DataType.DT_FLOAT, Shape.of(1,1)));
  }

  @Test
  void testUnsupportedDataTypeThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(new int[]{1}, DataType.DT_COMPLEX128, Shape.of(1,1)));
  }

  @Test
  void testCreateDoubleTensorWithInvalidTypeThrows() {
    String[] invalid = {"not", "valid"};
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_DOUBLE, Shape.of(1,2)));
  }
  @Test
  void testCreateFloatTensorWithInvalidTypeThrows() {
    String[] invalid = {"bad", "data"};
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_FLOAT, Shape.of(1,2)));
  }
  @Test
  void testCreateInt32TensorWithInvalidTypeThrows() {
    String[] invalid = {"nope"};
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_INT32, Shape.of(1)));
  }
  @Test
  void testCreateInt64TensorWithInvalidTypeThrows() {
    String[] invalid = {"fail"};
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_INT64, Shape.of(1)));
  }

  @Test
  void testCreateStringTensorWithInvalidTypeThrows() {
    Object invalid = 42;
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_STRING, Shape.of(1)));
  }

  @Test
  void testCreateBoolTensorWithInvalidTypeThrows() {
    Integer[] invalid = {1, 0};
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_BOOL, Shape.of(1,2)));
  }
  @Test
  void testCreateInt32TensorFromByteArray() {
    byte[] data = {3, 7};
    TInt32 tensor = (TInt32) TensorBuilder.createTensor(data, DataType.DT_INT32, Shape.of(1,2));
    assertEquals(3, tensor.getInt(0,0));
    assertEquals(7, tensor.getInt(0,1));
  }
  @Test
  void testCreateInt32TensorWithUnsupportedTypeThrows() {
    Object invalid = new char[]{'1', '2'};
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_INT32, Shape.of(1,2)));
  }
  @Test
  void testCreateInt64TensorWithUnsupportedTypeThrows() {
    Object invalid = new char[]{'1', '2'}; // char[] is not supported
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_INT64, Shape.of(1,2)));
  }

  @Test
  void testCreateDoubleTensorWithUnsupportedTypeThrows() {
    Object invalid = new char[]{'1', '2'}; // Not handled by any case
    assertThrows(IllegalArgumentException.class, () ->
        TensorBuilder.createTensor(invalid, DataType.DT_DOUBLE, Shape.of(1,2)));
  }
  @Test
  void testFloatTensorWithObjectNumberArray() {
    Object[] values = new Object[]{1.5f, 2.0d, 3}; // Float, Double, Integer
    Shape shape = Shape.of(1, 3);

    Tensor tensor = TensorBuilder.createTensor(values, DataType.DT_FLOAT, shape);
    assertTrue(tensor instanceof TFloat32);

    TFloat32 tf = (TFloat32) tensor;
    assertEquals(1.5f, tf.getFloat(0, 0), 0.0001);
    assertEquals(2.0f, tf.getFloat(0, 1), 0.0001);
    assertEquals(3.0f, tf.getFloat(0, 2), 0.0001);
  }

  @Test
  void testObjectArrayFloatTensor() {
    Object[] values = new Object[] {1.1f, 2.2f, 3.3f};
    Tensor t = TensorBuilder.createTensor(values, DataType.DT_FLOAT, Shape.of(1, 3));
    assertEquals(1.1f, ((TFloat32) t).getFloat(0, 0), 0.001f);
    assertEquals(2.2f, ((TFloat32) t).getFloat(0, 1), 0.001f);
    assertEquals(3.3f, ((TFloat32) t).getFloat(0, 2), 0.001f);
  }

  @Test
  void testObjectArrayDoubleTensor() {
    Object[] values = new Object[] {1.1, 2.2, 3.3};
    Tensor t = TensorBuilder.createTensor(values, DataType.DT_DOUBLE, Shape.of(1, 3));
    assertEquals(1.1, ((TFloat64) t).getDouble(0, 0), 0.001);
    assertEquals(2.2, ((TFloat64) t).getDouble(0, 1), 0.001);
    assertEquals(3.3, ((TFloat64) t).getDouble(0, 2), 0.001);
  }

  @Test
  void testObjectArrayIntTensor() {
    Object[] values = new Object[] {1, 2, 3};
    Tensor t = TensorBuilder.createTensor(values, DataType.DT_INT32, Shape.of(1, 3));
    assertEquals(1, ((TInt32) t).getInt(0, 0));
    assertEquals(2, ((TInt32) t).getInt(0, 1));
    assertEquals(3, ((TInt32) t).getInt(0, 2));
  }

  @Test
  void testObjectArrayLongTensor() {
    Object[] values = new Object[] {1L, 2L, 3L};
    Tensor t = TensorBuilder.createTensor(values, DataType.DT_INT64, Shape.of(1, 3));
    assertEquals(1L, ((TInt64) t).getLong(0, 0));
    assertEquals(2L, ((TInt64) t).getLong(0, 1));
    assertEquals(3L, ((TInt64) t).getLong(0, 2));
  }
}