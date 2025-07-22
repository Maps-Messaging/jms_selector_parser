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

import java.io.IOException;
import java.util.Iterator;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.tensorflow.Graph;
import org.tensorflow.GraphOperation;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.proto.DataType;

@Data
@AllArgsConstructor
public class TensorFlowModelEntry {
  private final SavedModelBundle model;
  private final GraphOperation inputOperation;
  private final GraphOperation outputOperation;
  private final String inputTensorName;
  private final String outputTensorName;

  private final DataType inputDataType;
  private final Shape inputShape;
  private final int featureCount;

  public TensorFlowModelEntry(SavedModelBundle model) throws IOException {
    this.model = model;
    Graph graph = model.graph();

    GraphOperation inputOp = null;
    GraphOperation outputOp = null;
    Iterator<GraphOperation> ops = graph.operations();

    while (ops.hasNext()) {
      GraphOperation op = ops.next();
      if (inputOp == null && op.type().equals("Placeholder")){
        inputOp = op;
      }
      if (outputOp == null && op.name().equals("StatefulPartitionedCall")) {
        outputOp = op;
      }
      if (inputOp != null && outputOp != null) break;
    }

    if (inputOp == null || outputOp == null) {
      throw new IOException("Could not resolve input/output names for model");
    }
    inputOperation = inputOp;
    outputOperation = outputOp;
    this.inputTensorName = inputOp.name();
    this.outputTensorName = outputOp.name(); // or append ":0" if needed by model

    this.inputDataType = inputOp.output(0).dataType();
    this.inputShape = inputOp.output(0).shape();

    if (inputShape.numDimensions() != 2 || inputShape.size(1) < 1) {
      throw new IOException("Unsupported input shape: " + inputShape);
    }

    this.featureCount = (int) inputShape.size(1);
  }
}

