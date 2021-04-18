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

    parser = SelectorParser.compile("floatVal = 4.0");
    Assertions.assertTrue(parser.evaluate(bean));

    parser = SelectorParser.compile("longtVal = 40");
    Assertions.assertFalse(parser.evaluate(bean));
  }


  public static final class Bean {
    private int iVal;
    private float fVal;

    public Bean(int i, float f){
      iVal = i;
      fVal = f;
    }

    public int getIntVal() {
      return iVal;
    }

    public void setIntVal(int iVal) {
      this.iVal = iVal;
    }

    public float getFloatVal() {
      return fVal;
    }

    public void setFloatVal(float fVal) {
      this.fVal = fVal;
    }
  }
}
