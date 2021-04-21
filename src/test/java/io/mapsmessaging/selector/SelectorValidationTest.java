/*
 *    Copyright [ 2020 - 2021 ] [Matthew Buckton]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

package io.mapsmessaging.selector;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.mapsmessaging.selector.operators.ParserBooleanOperation;
import io.mapsmessaging.selector.operators.ParserExecutor;
import io.mapsmessaging.selector.operators.ParserOperationExecutor;
import io.mapsmessaging.selector.extensions.ParserExtension;
import io.mapsmessaging.selector.operators.extentions.ParserFactory;

class SelectorValidationTest {


  @Test
  void checkBeans() throws ParseException {
    BeanTest bean = new BeanTest();
    bean.setCounter(0);
    ParserExecutor parser = SelectorParser.compile("counter = 10");
    for(int x=0;x<9;x++){
      bean.increment();
      Assertions.assertFalse(parser.evaluate(bean));
    }
    bean.increment();
    Assertions.assertTrue(parser.evaluate(bean));

  }

  public class BeanTest{
    private int counter;

    public void increment(){
      counter++;
    }

    public int getCounter() {
      return counter;
    }

    public void setCounter(int counter) {
      this.counter = counter;
    }
  }


  @Test
  void checkEmptyMessage() throws ParseException {
    ParserExecutor parser = SelectorParser.compile("key = 'found'");
    Assertions.assertTrue(parser instanceof ParserOperationExecutor);
    Assertions.assertFalse(parser.evaluate(new LinkedHashMap<>()));
  }

  @Test
  void checkSyntaxExceptions() {
    Assertions.assertThrows(ParseException.class, ()-> SelectorParser.compile("key 'found'"));
  }


  @Test
  void checkEmptyDataMapMessage() throws ParseException {
    ParserExecutor parser = SelectorParser.compile("key = 'found'");
    Assertions.assertTrue(parser instanceof ParserOperationExecutor);
    Assertions.assertFalse(parser.evaluate(new LinkedHashMap<>()), "Should not have returned true since there was no match");
  }

  @Test
  void checkTrueBooleanResults() throws ParseException {
    ParserExecutor parser1 = SelectorParser.compile("20 = 5 * 4");
    Assertions.assertTrue(parser1 instanceof ParserBooleanOperation);
    Map<String, Object> map = createMap("key1", 10L);
    map.put("key2", 5);
    Assertions.assertTrue(parser1.evaluate(map), "Should have evaluated to true, 10 = 5 * 4 == TRUE");

  }

  @Test
  void checkFalseBooleanResults() throws ParseException {
    ParserExecutor parser1 = SelectorParser.compile("10 = 50 * 4");
    Assertions.assertTrue(parser1 instanceof ParserBooleanOperation);
    Map<String, Object> map = createMap("key1", 10L);
    map.put("key2", 5);
    Assertions.assertFalse(parser1.evaluate(map), "Should have evaluated to true, 10 = 50 * 4 == FALSE");
  }


  @Test
  void checkArithmeticAdditionKeys() throws ParseException {
    ParserExecutor parser1 = SelectorParser.compile("key1 = key2 + 5");
    ParserExecutor parser2 = SelectorParser.compile("key1 = 5 + key2");
    Assertions.assertTrue(parser1 instanceof ParserOperationExecutor);
    Assertions.assertTrue(parser2 instanceof ParserOperationExecutor);
    Map<String, Object> map = createMap("key1", 10L);
    map.put("key2",5);
    Assertions.assertTrue(parser1.evaluate(map), "Should have evaluated to true, key1 = key2 + 5");
    Assertions.assertTrue(parser2.evaluate(map), "Should have evaluated to true, key1 = key2 + 5");
    Assertions.assertFalse(parser1.evaluate(createMap("key1", 10L)), "Should have failed evaluated to true, key1 = null + 5, since key2 == null");
    Assertions.assertFalse(parser2.evaluate(createMap("key1", 10L)), "Should have failed evaluated to true, key1 = null + 5, since key2 == null");
    Assertions.assertFalse(parser1.evaluate(createMap("key2", 10L)), "Should have failed evaluated to true =5 + 5, since key1 == null");
    Assertions.assertFalse(parser2.evaluate(createMap("key2", 10L)), "Should have failed evaluated to true =5 + 5, since key1 == null");
  }

  @Test
  void checkArithmeticSubtractionKeys() throws ParseException {
    ParserExecutor parser1 = SelectorParser.compile("key1 = key2 - 5");
    ParserExecutor parser2 = SelectorParser.compile("key1 = 5 - key2");
    Assertions.assertTrue(parser1 instanceof ParserOperationExecutor);
    Assertions.assertTrue(parser2 instanceof ParserOperationExecutor);

    Map<String, Object> map =createMap("key1", 0L);
    map.put("key2", 5);
    Assertions.assertTrue(parser1.evaluate(map), "Should have evaluated to true, key1 = key2 - 5");
    Assertions.assertTrue(parser2.evaluate(map), "Should have evaluated to true, key1 = key2 - 5");
    Assertions.assertFalse(parser1.evaluate(createMap("key1", 5)), "Should have failed evaluated to true, key1 = null - 5, since key2 == null");
    Assertions.assertFalse(parser2.evaluate(createMap("key1", 5)), "Should have failed evaluated to true, key1 = null - 5, since key2 == null");
    Assertions.assertFalse(parser1.evaluate(createMap("key2", 5)), "Should have failed evaluated to true =5 + 5, since key1 == null");
    Assertions.assertFalse(parser2.evaluate(createMap("key2", 5)), "Should have failed evaluated to true =5 + 5, since key1 == null");
  }

  @Test
  void checkArithmeticDivisionKeys() throws ParseException {
    ParserExecutor parser1 = SelectorParser.compile("key1 = key2 / 5");
    ParserExecutor parser2 = SelectorParser.compile("key1 = 5 / key2");
    Assertions.assertTrue(parser1 instanceof ParserOperationExecutor);
    Assertions.assertTrue(parser2 instanceof ParserOperationExecutor);

    Map<String, Object> map =  createMap("key1", 1L);
    map.put("key2",5);
    Assertions.assertTrue(parser1.evaluate(map), "Should have evaluated to true, key1 = key2 / 5");
    Assertions.assertTrue(parser2.evaluate(map), "Should have evaluated to true, key1 = 5 / key2 ");
    Assertions.assertFalse(parser1.evaluate(createMap("key1", 5)), "Should have failed evaluated to true, key1 = null - 5, since key2 == null");
    Assertions.assertFalse(parser2.evaluate(createMap("key1", 5)), "Should have failed evaluated to true, key1 = null - 5, since key2 == null");
    Assertions.assertFalse(parser1.evaluate(createMap("key2", 5)), "Should have failed evaluated to true =5 + 5, since key1 == null");
    Assertions.assertFalse(parser2.evaluate(createMap("key2", 5)), "Should have failed evaluated to true =5 + 5, since key1 == null");
  }

  @Test
  void checkArithmeticMultiplyKeys() throws ParseException {
    ParserExecutor parser1 = SelectorParser.compile("key1 = key2 * 5");
    ParserExecutor parser2 = SelectorParser.compile("key1 = 5 * key2");
    Assertions.assertTrue(parser1 instanceof ParserOperationExecutor);
    Assertions.assertTrue(parser2 instanceof ParserOperationExecutor);
    Map<String, Object> map = createMap("key1", 25L);
    map.put("key2", 5);
    Assertions.assertTrue(parser1.evaluate(map), "Should have evaluated to true, key1 = key2 / 5");
    Assertions.assertTrue(parser2.evaluate(map), "Should have evaluated to true, key1 = 5 / key2 ");
    Assertions.assertFalse(parser1.evaluate(createMap("key1", 25)), "Should have failed evaluated to true, key1 = null - 5, since key2 == null");
    Assertions.assertFalse(parser2.evaluate(createMap("key1", 25)), "Should have failed evaluated to true, key1 = null - 5, since key2 == null");
    Assertions.assertFalse(parser1.evaluate(createMap("key2", 5)), "Should have failed evaluated to true =5 + 5, since key1 == null");
    Assertions.assertFalse(parser2.evaluate(createMap("key2", 5)), "Should have failed evaluated to true =5 + 5, since key1 == null");
  }

  @Test
  void checkNumericEvaluations() throws ParseException {
    ParserExecutor parser = SelectorParser.compile("key = 1");
    Assertions.assertTrue(parser instanceof ParserOperationExecutor);
    Assertions.assertTrue(parser.evaluate(createMap("key", 1L)), "Should have evaluated to true, key=1");
    Assertions.assertTrue(parser.evaluate(createMap("key", 1)), "Should have evaluated to true, key=1");
    Assertions.assertTrue(parser.evaluate(createMap("key", (short)1)), "Should have evaluated to true, key=1");
    Assertions.assertTrue(parser.evaluate(createMap("key", (byte)1)), "Should have evaluated to true, key=1");
    Assertions.assertTrue(parser.evaluate(createMap("key", 1.0f)), "Should have evaluated to true, key=1");
    Assertions.assertTrue(parser.evaluate(createMap("key", 1.0)), "Should have evaluated to true, key=1");
  }

  @Test
  void checkNumericMissedEvaluations() throws ParseException {
    ParserExecutor parser = SelectorParser.compile("key = 2");
    Assertions.assertTrue(parser instanceof ParserOperationExecutor);
    Assertions.assertFalse(parser.evaluate(createMap("key", 1L)), "Should have evaluated to true, key=1");
    Assertions.assertFalse(parser.evaluate(createMap("key", 1)), "Should have evaluated to true, key=1");
    Assertions.assertFalse(parser.evaluate(createMap("key", (short)1)), "Should have evaluated to true, key=1");
    Assertions.assertFalse(parser.evaluate(createMap("key", (byte)1)), "Should have evaluated to true, key=1");
    Assertions.assertFalse(parser.evaluate(createMap("key", 1.0f)), "Should have evaluated to true, key=1");
    Assertions.assertFalse(parser.evaluate(createMap("key", 1.0)), "Should have evaluated to true, key=1");
  }


  @Test
  void checkParserExtensions(){
    Iterator<ParserExtension> iterator = ParserFactory.getInstance().getServices();
    while(iterator.hasNext()){
      ParserExtension service = iterator.next();
      Assertions.assertTrue(service instanceof ParserExtension);

    }
  }

  private Map<String, Object> createMap(String key, Object val){
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(key, val);
    return map;
  }
}
