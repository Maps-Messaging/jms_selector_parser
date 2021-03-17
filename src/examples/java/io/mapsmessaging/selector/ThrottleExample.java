package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThrottleExample {
  @Test
  void simpleExampleOfThrottle() throws ParseException, InterruptedException {
    String selector =  "TRUE = extension ('throttle', '15', '10000')";
    ParserExecutor parser = SelectorParser.compile(selector);

    // We should be able to push 15 messages and then fail for the rest of the minute
    for (int x = 0; x < 15; x++) {
      Assertions.assertTrue(parser.evaluate(key -> "Hi"));
    }

    // Wait just over 10 seconds and we should be free again
    long start = System.currentTimeMillis();
    while (!parser.evaluate(key -> "Hi")) {
      Thread.sleep(10);
    }
    Assertions.assertTrue((System.currentTimeMillis() - start) >= 10000);

    // We take 1 away since that is how it exited the loop above
    for (int x = 0; x < 14; x++) {
      Assertions.assertTrue(parser.evaluate(key -> "Hi"));
    }
    // This should fail since we have exceeded the limit
    Assertions.assertFalse(parser.evaluate(key -> "Hi"));
  }
}
