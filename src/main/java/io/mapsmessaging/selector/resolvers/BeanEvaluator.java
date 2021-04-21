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
package io.mapsmessaging.selector.resolvers;

import io.mapsmessaging.selector.IdentifierResolver;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class BeanEvaluator implements IdentifierResolver {

  private static final Map<String, Map<String, Method>> LOADED_MAPPINGS = new ConcurrentHashMap<>();

  private final Map<String, Method> mapping;
  private final Object bean;

  public BeanEvaluator(Object bean){
    this.bean = bean;
    mapping = getMapping(bean);
  }

  private static Map<String, Method> getMapping(Object bean)  {
    if (LOADED_MAPPINGS.containsKey(bean.getClass().getName())) {
      return LOADED_MAPPINGS.get(bean.getClass().getName());
    }
    Map<String, Method> map = new LinkedHashMap<>();
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
      PropertyDescriptor[] list = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor propertyDescriptor : list) {
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod != null) {
          map.put(propertyDescriptor.getName(), readMethod);
        }
      }
    } catch (IntrospectionException e) {
      // seems we can not access the beans functions here
    }
    LOADED_MAPPINGS.put(bean.getClass().getName(), map);
    return map;
  }

  @Override
  public Object get(String key) {
    Method method = mapping.get(key);
    try {
      if(method != null) {
        return method.invoke(bean);
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      // Seems we can not access the beans method here
    }
    return null;
  }
}
