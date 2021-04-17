package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BeanTest {

  @Test
  void simpleBeanCheck() throws ParseException {
    Bean bean = new Bean(2, 4.0f);
    ParserExecutor parser = SelectorParser.compile("intVal = 2");
    Assertions.assertTrue(parser.evaluate(bean));
  }


  public final class Bean {
    private int intVal;
    private float floatVal;

    public Bean(int i, float f){
      intVal = i;
      floatVal = f;
    }

    public int getIntVal() {
      return intVal;
    }

    public void setIntVal(int intVal) {
      this.intVal = intVal;
    }

    public float getFloatVal() {
      return floatVal;
    }

    public void setFloatVal(float floatVal) {
      this.floatVal = floatVal;
    }
  }
}
