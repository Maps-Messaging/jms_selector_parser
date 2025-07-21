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

package io.mapsmessaging.selector.operators.functions.ml.impl.store;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.tensorflow.SavedModelBundle;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.io.Read;
import smile.io.Write;

public class ModelUtils {
  private static final long MAX_ENTRY_SIZE = 1024L * 1024L * 1024L;

  @Getter
  @Setter
  private static long thresholdsize = 100_000_000L; // Maximum allowed uncompressed size (adjust as needed)
  @Getter
  @Setter
  private static int thresholdentries = 10_000; // Maximum allowed entries in the archive
  @Getter
  @Setter
  private static double thresholdRatio = 100.0; // Maximum allowed compression ratio


  private ModelUtils() {}

  // Load a TensorFlow model from a byte array
  @SuppressWarnings({"java:S5443", "java.S5042"}) // yes it is safe to do, I check the max size of the zip
  public static SavedModelBundle byteArrayToModel(byte[] data, String modelDir) throws IOException {
    // Constants for threshold checks

    // Create a temporary directory
    Path tempDir = Files.createTempDirectory(modelDir + File.separator + "tf_model");
    if(!tempDir.toFile().setReadable(true, true) &&
        !tempDir.toFile().setWritable(true, true) &&
        !tempDir.toFile().setExecutable(true, true)){
      throw new IOException("Unable to secure temp directory");
    }

    tempDir.toFile().deleteOnExit();

    long totalSizeArchive = 0;
    int totalEntryArchive = 0;

    // Decompress the byte array into the temporary directory
    try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(data))) {
      ZipEntry zipEntry;
      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        if (zipEntry.getSize() > MAX_ENTRY_SIZE) {
          throw new IOException("ZIP entry too large: " + zipEntry.getName());
        }
        totalEntryArchive++;
        if (totalEntryArchive > thresholdentries) {
          throw new IOException("Too many entries in ZIP file");
        }
        totalSizeArchive = processEntry(tempDir, zipInputStream, zipEntry, totalSizeArchive);
        zipInputStream.closeEntry();
      }
    }

    // Load the model from the temporary directory
    return SavedModelBundle.load(tempDir.toString(), "serve");
  }

  private static long processEntry(Path tempDir, ZipInputStream zipInputStream, ZipEntry zipEntry, long totalSizeArchive) throws IOException {
    // Check number of entries to prevent Zip Bomb attack

    Path filePath = tempDir.resolve(zipEntry.getName());
    if (zipEntry.isDirectory()) {
      Files.createDirectories(filePath);
    } else {
      Files.createDirectories(filePath.getParent());

      // Calculate compression ratio and check for suspicious entries
      long uncompressedSize = zipEntry.getSize();
      long compressedSize = zipEntry.getCompressedSize();
      if (uncompressedSize > 0 && compressedSize > 0) {
        double compressionRatio = (double) uncompressedSize / compressedSize;
        if (compressionRatio > thresholdRatio) {
          throw new IOException("Suspicious ZIP entry detected, possible ZIP bomb");
        }
      }

      // Prevent oversized uncompressed data
      if (totalSizeArchive + uncompressedSize > thresholdsize) {
        throw new IOException("Uncompressed data exceeds allowed limit");
      }

      // Extract the file
      try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath))) {
        byte[] buffer = new byte[2048];
        int nBytes;
        while ((nBytes = zipInputStream.read(buffer)) > 0) {
          out.write(buffer, 0, nBytes);
        }
      }
      totalSizeArchive += uncompressedSize;
    }
    return totalSizeArchive;
  }

  @SuppressWarnings({"java:S5443"})
  public static byte[] dataFrameToBytes(DataFrame df, String format) throws IOException {
    if(format == null || format.isEmpty()){
      format = "arff";
    }
    switch (format.toLowerCase(Locale.ROOT)) {
      case "csv" : {
        Path temp = Files.createTempFile("df", ".csv");
        Write.csv(df, temp, CSVFormat.DEFAULT);
        byte[] bytes = Files.readAllBytes(temp);
        Files.deleteIfExists(temp);
        return bytes;
      }
      case "arff" : {
        Path temp = Files.createTempFile("df", ".arff");
        String arrf = toWekaArff(df, "auto generated by maps messaging");
        byte[] bytes = arrf.getBytes();
        Files.deleteIfExists(temp);
        return bytes;
      }
      case "arrow" : {
        Path temp = Files.createTempFile("df", ".feather");
        Write.arrow(df, temp);
        byte[] bytes = Files.readAllBytes(temp);
        Files.deleteIfExists(temp);
        return bytes;
      }
      default: throw new IllegalArgumentException("Unsupported format: " + format);
    }
  }

  @SuppressWarnings({"java:S5443"})
  public static DataFrame dataFrameFromBytes(byte[] data, String format) throws IOException {
    if(format == null || format.isEmpty()){
      format = "arff";
    }
    Path temp = Files.createTempFile("df", "." + format.toLowerCase(Locale.ROOT));
    try {
      Files.write(temp, data);
      return Read.data(temp.toString(), format);
    } catch (URISyntaxException | ParseException e) {
      throw new IOException(e);
    } finally {
      Files.deleteIfExists(temp);
    }
  }

  public static DataFrame dataFrameFromBuffer(List<double[]> buffer, List<String> columnNames) {
    int numRows = buffer.size();
    if (numRows == 0) {
      throw new IllegalArgumentException("No data in buffer");
    }
    int numCols = buffer.getFirst().length;
    double[][] matrix = new double[numRows][numCols];
    for (int i = 0; i < numRows; i++) {
      matrix[i] = buffer.get(i);
    }
    return DataFrame.of(matrix, columnNames.toArray(new String[0]));
  }

  public static String toWekaArff(DataFrame df, String relationName) {
    StringBuilder sb = new StringBuilder();
    sb.append("@RELATION ").append("\"").append(relationName).append("\"").append("\n\n");

    for (String name : df.names()) {
      sb.append("@ATTRIBUTE ").append(name).append(" REAL\n");
    }

    sb.append("\n@DATA\n");
    for (int i = 0; i < df.nrow(); i++) {
      Tuple row = df.get(i);
      for (int j = 0; j < row.length(); j++) {
        if (j > 0) sb.append(",");
        sb.append(row.get(j));
      }
      sb.append("\n");
    }

    return sb.toString();
  }

}
