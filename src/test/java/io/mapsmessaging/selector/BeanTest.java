package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BeanTest {

  @Test
  void simpleBeanCheck() throws ParseException {
    Bean bean = new Bean(2, 4.0f);
    ParserExecutor parser = SelectorParser.compile("intVal = 2");
    Assertions.assertTrue(parser.evaluate(bean));

    parser = SelectorParser.compile("floatVal = 4.0f");
    Assertions.assertTrue(parser.evaluate(bean));

    parser = SelectorParser.compile("longtVal = 40");
    Assertions.assertFalse(parser.evaluate(bean));
  }

  @Test
  void walkingBeanCheck() throws ParseException {
    Bean bean = new Bean(2, 4.0f);
    InnerBean innerBean = new InnerBean();
    innerBean.setValue(bean);
    ParserExecutor parser = SelectorParser.compile("value#intVal = 2");
    Assertions.assertTrue(parser.evaluate(innerBean));

    parser = SelectorParser.compile("value#floatVal = 4.0f");
    Assertions.assertTrue(parser.evaluate(innerBean));

    parser = SelectorParser.compile("value#longtVal = 40");
    Assertions.assertFalse(parser.evaluate(innerBean));
  }


  public static final class Bean {
    @Getter @Setter private long intVal;
    @Getter @Setter private float floatVal;

    public Bean(int i, float f) {
      intVal = i;
      floatVal = f;
    }
  }

  public static final class InnerBean{
    @Getter @Setter Bean value;
  }
}
