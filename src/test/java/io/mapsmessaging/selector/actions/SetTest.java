package io.mapsmessaging.selector.actions;

import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.resolvers.EvaluatorFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SetTest {

  @Test
  void simpleSetTest() throws ParseException {
    SetAction setAction = new SetAction("addition", "Additional value");
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("remove", "remove".getBytes());
    setAction.evaluate(EvaluatorFactory.create(map));
    Assertions.assertEquals(2, map.size());
  }

}
