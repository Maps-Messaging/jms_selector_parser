/*
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.mapsmessaging.selector;

import com.github.javafaker.Faker;
import io.mapsmessaging.selector.operators.ParserExecutor;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonFilteringTest {

  private final static int LIST_SIZE = 1000;

  @Test
  void simpleJsonFiltering() throws ParseException {
    JSONArray addressList = buildList();
    int alaskaCount = 0;
    int filtered = 0;
    ParserExecutor executor = SelectorParser.compile("state = 'Alaska'");
    for(int x=0;x<addressList.length();x++){
      JSONObject jsonObject = addressList.getJSONObject(x);
      if(jsonObject.get("state").equals("Alaska")){
        alaskaCount++;
      }
      if(executor.evaluate(jsonObject)){
        filtered++;
      }
    }
    Assertions.assertEquals(alaskaCount, filtered);
  }


  @Test
  void nestedJsonFiltering() throws ParseException {
    Faker faker = new Faker();
    JSONArray addressList = buildList();
    JSONArray people = new JSONArray();
    for(int x=0;x<addressList.length();x++){
      JSONObject address = addressList.getJSONObject(x);
      JSONObject person = new JSONObject();
      person.put("address", address);
      person.put("first", faker.name().firstName());
      person.put("last", faker.name().lastName());
      person.put("phone", faker.phoneNumber().phoneNumber());
      person.put("email", faker.internet().emailAddress());
      people.put(person);
    }


    int alaskaCount = 0;
    int filtered = 0;
    ParserExecutor executor = SelectorParser.compile("address.state = 'Alaska'");
    for(int x=0;x<people.length();x++){
      JSONObject person = people.getJSONObject(x);
      if(person.getJSONObject("address").get("state").equals("Alaska")){
        alaskaCount++;
      }
      if(executor.evaluate(person)){
        filtered++;
      }
    }
    Assertions.assertEquals(alaskaCount, filtered);
  }


  private static JSONArray buildList(){
    JSONArray addressList = new JSONArray();
    Faker faker = new Faker();
    for (int x = 0; x < LIST_SIZE; x++) {
      JSONObject jsonObject = new JSONObject();
      Address address = new Address(faker.address());
      jsonObject.put("street", address.getStreet());
      jsonObject.put("suburb", address.getSuburb());
      jsonObject.put("zipCode", address.getZipCode());
      jsonObject.put("state", address.getState());
      addressList.put(jsonObject);
    }
    return addressList;
  }

  @Data
  public static class Address{
    final String street;
    final String suburb;
    final String zipCode;
    final String state;

    public Address(com.github.javafaker.Address address) {
      this.state = address.state();
      this.street = address.streetAddress();
      this.suburb = address.city();
      this.zipCode = address.zipCode();

    }
  }
}
