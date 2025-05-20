package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import io.mapsmessaging.selector.operators.functions.ml.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.ModelStore;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.FileModelStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DecisionTreeModelLoadTest {
  private final static String[] SELECTORS ={
      "decision_tree (classify, scd41.arff , CO₂ , temperature, humidity) > 0 OR NOT model_exists(scd41.arff)",
      "decision_tree (classifyprob, scd41.arff , CO₂ , temperature, humidity) > 0 OR NOT model_exists(scd41.arff)",
  } ;


  @Test
  void testLoadModel() throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      Assertions.assertTrue(MLFunction.getModelStore().modelExists("scd41.arff"));
      Assertions.assertNotNull(MLFunction.getModelStore().loadModel("scd41.arff"));
      for(String selector : SELECTORS) {
        ParserExecutor executor = SelectorParser.compile(selector);
        Assertions.assertNotNull(executor);
      }
    } finally {
      MLFunction.setModelStore(previous);
    }
  }

  @Test
  void testRunModel() throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      ParserExecutor executor = SelectorParser.compile("classifyprob(classify, scd41.arff , CO₂ , temperature, humidity) < 2");
      Assertions.assertTrue(executor.evaluate((IdentifierResolver) key -> {
        switch (key) {
          case "CO₂":
            return 566;
          case "temperature":
            return 20.9;
          case "humidity":
            return 55.6;
          default:
            return Double.NaN;
        }
      }));

      Assertions.assertFalse(executor.evaluate((IdentifierResolver) key -> {
        switch (key) {
          case "CO₂":
            return 1200;
          case "temperature":
            return 20.9;
          case "humidity":
            return 55.6;
          default:
            return Double.NaN;
        }
      }));
    } finally {
      MLFunction.setModelStore(previous);
    }
  }
}
