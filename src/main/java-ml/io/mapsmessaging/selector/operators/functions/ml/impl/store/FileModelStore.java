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

import io.mapsmessaging.selector.operators.functions.ml.ModelStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileModelStore implements ModelStore {

  private final String rootDirectory;

  public FileModelStore(String rootDirectory) {
    this.rootDirectory = rootDirectory;
  }

  @Override
  public void saveModel(String s, byte[] bytes) throws IOException {
    File file = new File(rootDirectory, s);
    file.getParentFile().mkdirs();
    try(FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(bytes);
    }
  }

  @Override
  public byte[] loadModel(String s) throws IOException {
    File file = new File(rootDirectory, s);
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
  public boolean modelExists(String s) {
    File file = new File(rootDirectory, s);
    return file.exists();
  }
}
