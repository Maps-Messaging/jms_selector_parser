/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2025 ] MapsMessaging B.V.
 *
 *  Licensed under the Apache License, Version 2.0 with the Commons Clause
 *  (the "License"); you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      https://commonsclause.com/
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.mapsmessaging.selector;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.mapsmessaging.selector.operators.ParserExecutor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonFilteringTest {

  private final static int LIST_SIZE = 1000;

  @Test
  void simpleJsonFiltering() throws ParseException {
    JsonArray addressList = buildList();
    int alaskaCount = 0;
    int filtered = 0;
    ParserExecutor executor = SelectorParser.compile("state = 'Alaska'");
    for(int x=0;x<addressList.size();x++){
      JsonObject jsonObject = addressList.get(x).getAsJsonObject();
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
    JsonArray addressList = buildList();
    JsonArray people = new JsonArray();
    for (int x = 0; x < addressList.size(); x++) {
      JsonObject address = addressList.get(x).getAsJsonObject();
      JsonObject person = new JsonObject();

      person.add("address", address);
      person.addProperty("first", faker.name().firstName());
      person.addProperty("last", faker.name().lastName());
      person.addProperty("phone", faker.phoneNumber().phoneNumber());
      person.addProperty("email", faker.internet().emailAddress());

      people.add(person); // assuming people is a JsonArray
    }



    int alaskaCount = 0;
    int filtered = 0;
    ParserExecutor executor = SelectorParser.compile("address.state = 'Alaska'");
    for(int x=0;x<people.size();x++){
      JsonObject person = people.get(x).getAsJsonObject();
      if(person.getAsJsonObject("address").get("state").equals("Alaska")){
        alaskaCount++;
      }
      if(executor.evaluate(person)){
        filtered++;
      }
    }
    Assertions.assertEquals(alaskaCount, filtered);
  }


  private static JsonArray buildList(){
    JsonArray addressList = new JsonArray();
    Faker faker = new Faker();
    Gson gson = new Gson();
    for (int x = 0; x < LIST_SIZE; x++) {
      Address address = new Address(faker.address());
      JsonElement jsonElement = gson.toJsonTree(address);
      addressList.add(jsonElement);
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
