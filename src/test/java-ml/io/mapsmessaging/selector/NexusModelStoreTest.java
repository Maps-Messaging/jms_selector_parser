package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.functions.ml.impl.store.NexusModelStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusModelStoreTest {

  private NexusModelStore modelStore;

  @BeforeAll
  void setup() {
    String user = System.getenv("MODEL_USER");
    String pass = System.getenv("MODEL_PASSWORD");
    assertNotNull(user, "Missing model.user system property");
    assertNotNull(pass, "Missing model.pass system property");

    modelStore = new NexusModelStore("https://repository.mapsmessaging.io/repository/maps_ml_store/");
    modelStore.login(user, pass);
  }


  @Test
  void testPushListDeleteModels() throws Exception {
    uploadAndVerify("isoTest.arff");
    uploadAndVerify("sensor_safety_model.zip");

    deleteAndVerify("isoTest.arff");
    deleteAndVerify("sensor_safety_model.zip");
  }

  private void uploadAndVerify(String name) throws IOException {
    byte[] content = Files.readAllBytes(Path.of("src/test/resources/" + name));
    modelStore.saveModel(name, content);

    assertTrue(modelStore.modelExists(name));
    byte[] loaded = modelStore.loadModel(name);
    assertArrayEquals(content, loaded);
  }

  private void deleteAndVerify(String name) throws IOException {
    modelStore.deleteModel(name);
    assertFalse(modelStore.modelExists(name));
  }
}
