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

package io.mapsmessaging.selector.operators.functions.ml.impl.store;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.tensorflow.SavedModelBundle;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

public class ModelUtils {

  public static byte[] instancesToByteArray(Instances instances) throws IOException {
    ArffSaver saver = new ArffSaver();
    saver.setInstances(instances);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    saver.setDestination(byteArrayOutputStream);
    saver.writeBatch();

    return byteArrayOutputStream.toByteArray();
  }

  public static Instances byteArrayToInstances(byte[] data) throws IOException {
    ArffLoader loader = new ArffLoader();
    loader.setSource(new ByteArrayInputStream(data));
    return loader.getDataSet();
  }

  // Load a TensorFlow model from a byte array
  public static SavedModelBundle byteArrayToModel(byte[] data, String modelDir) throws IOException {
    // Create a temporary directory
    Path tempDir = Files.createTempDirectory(modelDir + File.separator + "tf_model");
    tempDir.toFile().deleteOnExit();

    // Decompress the byte array into the temporary directory
    try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(data))) {
      ZipEntry zipEntry;
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        Path filePath = tempDir.resolve(zipEntry.getName());
        if (zipEntry.isDirectory()) {
          Files.createDirectories(filePath);
        } else {
          Files.createDirectories(filePath.getParent());
          Files.copy(zipInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        zipInputStream.closeEntry();
      }
    }
    // Load the model from the temporary directory
    return SavedModelBundle.load(tempDir.toString(), "serve");
  }

  private ModelUtils() {}
}
