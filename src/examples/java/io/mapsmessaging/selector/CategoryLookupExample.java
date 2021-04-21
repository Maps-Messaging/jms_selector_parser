/*
 *
 *   Copyright [ 2020 - 2021 ] [Matthew Buckton]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package io.mapsmessaging.selector;

import io.mapsmessaging.selector.extensions.CategoryLookupExtension;
import io.mapsmessaging.selector.operators.ParserExecutor;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CategoryLookupExample {

  private static final String[] BIRDS = {
      "Palm cockatoo","Red-tailed black-cockatoo","Glossy black-cockatoo","Yellow-tailed black-cockatoo","Carnaby's black-cockatoo","Baudin's black-cockatoo","Gang-gang cockatoo",
      "Pink cockatoo","Galah","Long-billed corella","Western corella","Little corella","Sulphur-crested cockatoo","Cockatiel"
  };

  private static final String[] CATS = {
      "Birman","British Shorthair","Exotic Shorthair","Munchkin","Ragamuffin","Ragdoll","Scottish Fold","American Bobtail","Bengal","Chausie", "Ragamuffin","Ragdoll","Savannah",
      "Siberian","American Bobtail","American Curl","American Shorthair","Bombay","Exotic Shorthair", "Scottish Fold","Selkirk Rex","Birman","Exotic Shorthair","Maine Coon",
      "Norwegian Forest Cat","Persian","Ragdoll","Siberian","Munchkin", "Ragdoll","Snowshoe"
  };

  private static final String[] DOGS = {
      "Affenpinscher","Afghan Hound","Airedale Terrier","Akita","Alaskan Malamute","American Hairless Terrier","American Staffordshire Terrier", "Fox Terrier",
      "Foxhound","French Bulldog","German Hunting Terrier","German Pinscher","German Shepherd Dog","German Shorthaired Pointer", "Leonberger","Lhasa Apso",
      "Lowchen","Maltese","Maltipoo","Manchester Terrier","Maremma Sheepdog","Mastiff","Miniature Schnauzer","Moodle","Neapolitan Mastiff",
      "Whippet","White Swiss Shepherd Dog","Wirehaired Slovakian Pointer","Yorkshire Terrier","Zuchon"
  };

  @BeforeAll
  static void setupCategories(){
    Map<String, String> cats = new LinkedHashMap<>();
    for(String cat:CATS){
      cats.put(cat, "cat");
    }

    Map<String, String> dogs = new LinkedHashMap<>();
    for(String dog:DOGS){
      dogs.put(dog, "dog");
    }

    Map<String, String> birds = new LinkedHashMap<>();
    for(String bird:BIRDS){
      birds.put(bird, "bird");
    }

    // Preregister categories
    CategoryLookupExtension.registerCategory("cats", cats);
    CategoryLookupExtension.registerCategory("dogs", dogs);
    CategoryLookupExtension.registerCategory("birds", birds);
  }

  @Test
  void simpleExampleOfCategoryLookup() throws ParseException {
    String selector =  "'dog' = extension ('category', 'animalName', 'birds', 'dogs', 'cats' )";
    ParserExecutor parser = SelectorParser.compile(selector);

    // This should fail since a "ragamuffin" is a cat NOT a dog
    Assertions.assertFalse(
    parser.evaluate((IdentifierResolver)key -> {
      if(key.equals("animalName")){
        return "Ragamuffin";
      }
      return null;
    }));

    // This should work since a German Shepherd Dog is in fact a dog
    Assertions.assertTrue(
        parser.evaluate((IdentifierResolver)key -> {
          if(key.equals("animalName")){
            return "German Shepherd Dog";
          }
          return null;
        }));
  }
}
