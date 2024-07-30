package io.mapsmessaging.selector.operators.functions.ml.impl.store;

import org.tensorflow.SavedModelBundle;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
    Path tempDir = Files.createTempDirectory(modelDir+File.separator+"tf_model");
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

  private ModelUtils(){}
}
