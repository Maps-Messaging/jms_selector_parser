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
package io.mapsmessaging.selector.resolvers;

import io.mapsmessaging.selector.IdentifierMutator;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class BeanEvaluator implements IdentifierMutator {

  private static final Map<String, Map<String, Method>> LOADED_GET_MAPPINGS =
      new ConcurrentHashMap<>();
  private static final Map<String, Map<String, Method>> LOADED_SET_MAPPINGS =
      new ConcurrentHashMap<>();
  private final Object parent;

  public BeanEvaluator(Object bean) {
    this.parent = bean;
  }

  private static Map<String, Method> getMapping(Object bean) {
    if (LOADED_GET_MAPPINGS.containsKey(bean.getClass().getName())) {
      return LOADED_GET_MAPPINGS.get(bean.getClass().getName());
    }
    loadMaps(bean);
    return LOADED_GET_MAPPINGS.get(bean.getClass().getName());
  }

  private static Map<String, Method> setMapping(Object bean) {
    if (LOADED_SET_MAPPINGS.containsKey(bean.getClass().getName())) {
      return LOADED_SET_MAPPINGS.get(bean.getClass().getName());
    }
    loadMaps(bean);
    return LOADED_SET_MAPPINGS.get(bean.getClass().getName());
  }

  private static void loadMaps(Object bean) {
    Map<String, Method> get = new LinkedHashMap<>();
    Map<String, Method> set = new LinkedHashMap<>();
    try {
      var beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
      PropertyDescriptor[] list = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor propertyDescriptor : list) {
        var readMethod = propertyDescriptor.getReadMethod();
        if (readMethod != null) {
          get.put(propertyDescriptor.getName(), readMethod);
        }
        var writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod != null) {
          set.put(propertyDescriptor.getName(), writeMethod);
        }
      }
    } catch (IntrospectionException e) {
      // seems we can not access the beans functions here
    }
    LOADED_GET_MAPPINGS.put(bean.getClass().getName(), get);
    LOADED_SET_MAPPINGS.put(bean.getClass().getName(), set);
  }

  @Override
  public Object get(String key) {
    if (key.contains("#")) {
      String[] keyDepth = key.split("#");
      Object bean = parent;
      for (String keyWalk : keyDepth) {
        bean = lookup(keyWalk, bean);
        if (bean == null) {
          return null;
        }
      }
      return bean;
    } else {
      return lookup(key, parent);
    }
  }

  @Override
  public Object remove(String key) {
    return null; // This is a bean
  }

  @Override
  public Object set(String key, Object value) {
    if (key.contains("#")) {
      String[] keyDepth = key.split("#");
      Object bean = parent;
      for (String keyWalk : keyDepth) {
        bean = lookupAndSet(keyWalk, bean, value);
        if (bean == null) {
          return null;
        }
      }
      return bean;
    } else {
      return lookupAndSet(key, parent, value);
    }
  }

  private Object lookup(String key, Object bean) {
    var method = getMapping(bean).get(key);
    try {
      if (method != null) {
        return method.invoke(bean);
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      // Seems we can not access the beans method here
    }
    return null;
  }

  private Object lookupAndSet(String key, Object bean, Object value) {
    var method = setMapping(bean).get(key);
    try {

      if (method != null) {
        return method.invoke(bean, value);
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      // Seems we can not access the beans method here
    }
    return null;
  }
}
