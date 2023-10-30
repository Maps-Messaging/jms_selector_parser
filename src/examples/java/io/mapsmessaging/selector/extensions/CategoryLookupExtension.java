/*
 *
 *   Copyright [ 2020 - 2023 ] [Matthew Buckton]
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

package io.mapsmessaging.selector.extensions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;

public class CategoryLookupExtension  implements ParserExtension {

  private static final Map<String, Map<String, String>> REGISTERED_CATEGORIES = new LinkedHashMap<>();
  public static void registerCategory(String name, Map<String, String> categories){
    REGISTERED_CATEGORIES.put(name, categories);
  }


  private final String keyName;
  private final List<String> categories;

  public CategoryLookupExtension(){
    categories = new ArrayList<>();
    keyName = "";
  }

  private CategoryLookupExtension(List<String> arguments){
    categories = new ArrayList<>();
    keyName = arguments.get(0);
    for(int x=1;x<arguments.size();x++){
      categories.add(arguments.get(x));
    }
  }

  @Override
  public ParserExtension createInstance(List<String> arguments) throws ParseException {
    return new CategoryLookupExtension(arguments);
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) {
    Object result = resolver.get(keyName);
    if(result != null){
      for(String lookup:categories){
        Map<String, String> map = REGISTERED_CATEGORIES.get(lookup);
        if(map != null){
          Object ret = map.get(result.toString());
          if(ret != null){
            return ret;
          }
        }
      }
    }
    return false;
  }

  @Override
  public String getName() {
    return "category";
  }

  @Override
  public String getDescription() {
    return "Looks up the identifier supplied for the values category";
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder("category, '");
    sb.append("key:").append(keyName).append(' ');
    for(String category:categories){
      sb.append(category).append("' , '");
    }
    return sb.toString();
  }
}
