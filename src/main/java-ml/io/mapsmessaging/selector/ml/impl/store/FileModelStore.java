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

package io.mapsmessaging.selector.ml.impl.store;

import io.mapsmessaging.selector.model.ModelStore;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileModelStore implements ModelStore {

  private final String rootDirectory;

  public FileModelStore(String rootDirectory) {
    this.rootDirectory = rootDirectory;
  }

  @Override
  public void saveModel(String modelId, byte[] bytes) throws IOException {
    File file = resolveModelPath(modelId);
    file.getParentFile().mkdirs();
    try(FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(bytes);
    }
  }

  @Override
  public byte[] loadModel(String modelId) throws IOException {
    File file = resolveModelPath(modelId);
    byte[] buffer;
    try(FileInputStream fis = new FileInputStream(file)) {
      long pos = 0;
      buffer = new byte[(int)file.length()];
      while(pos < file.length()) {
        int read = fis.read(buffer, 0, (int)file.length());
        if(read < 0){
          throw new IOException("Could not read file " + file.getAbsolutePath());
        }
        pos += read;
      }
    }
    return buffer;
  }

  @Override
  public boolean modelExists(String modelId) {
    File file = resolveModelPath(modelId);
    if(file.exists()){
      return true;
    }
    file = new File(rootDirectory, modelId+".zip");
    return file.exists();
  }

  @Override
  public boolean deleteModel(String modelId) throws IOException {
    File file = resolveModelPath(modelId);
    if(file.exists()) {
      Files.delete(file.toPath());
      return true;
    }
    return false;
  }

  private File resolveModelPath(String modelId) {
    int lastDot = modelId.lastIndexOf('.');
    String path = (lastDot <= 0)
        ? modelId.replace('.', File.separatorChar)
        : modelId.substring(0, lastDot).replace('.', File.separatorChar) + modelId.substring(lastDot);
    return new File(rootDirectory, path);
  }


  @Override
  public List<String> listModels() throws IOException {
    List<String> result = new ArrayList<>();
    Path root = new File(rootDirectory).toPath();
    try (Stream<Path> paths = Files.walk(root)) {
      paths.filter(Files::isRegularFile)
          .forEach(path -> {
            Path rel = root.relativize(path);
            String name = rel.toString().replace(File.separatorChar, '.');
            result.add(name);
          });
    }
    return result;
  }
}
