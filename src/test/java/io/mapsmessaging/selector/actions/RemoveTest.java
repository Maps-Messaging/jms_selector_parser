package io.mapsmessaging.selector.actions;

import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.resolvers.EvaluatorFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RemoveTest {

  @Test
  void simpleRemoveTest() throws ParseException {
    RemoveAction removeAction = new RemoveAction("remove");
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("remove", "remove".getBytes());
    removeAction.evaluate(EvaluatorFactory.create(map));
    Assertions.assertEquals(0, map.size());
  }
}
