package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CounterExample {


  @Test
  void simpleExampleOfCategoryLookup() throws ParseException {
    String selector =  "10 = extension ('counter', '')";
    ParserExecutor parser = SelectorParser.compile(selector);

    // This should fail the counter is less then 10
    for (int x = 0; x < 10; x++) {
      Assertions.assertFalse(parser.evaluate(key -> "Hi"));
    }
    // This should work since the counter is in fact 10
    Assertions.assertTrue(parser.evaluate(key -> "Hi"));
  }
}
