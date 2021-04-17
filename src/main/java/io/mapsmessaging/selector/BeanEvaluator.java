package io.mapsmessaging.selector;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanEvaluator implements IdentifierResolver {

  public static final Map<String, Map<String, Method>> LOADED_MAPPINGS = new ConcurrentHashMap<>();

  private final Map<String, Method> mapping;
  private final Object bean;

  public BeanEvaluator(Object bean){
    this.bean = bean;
    mapping = getMapping(bean);
  }

  private static Map<String, Method> getMapping(Object bean)  {
    synchronized (LOADED_MAPPINGS) {
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
        e.printStackTrace();
      }
      LOADED_MAPPINGS.put(bean.getClass().getName(), map);
      return map;
    }
  }

  @Override
  public Object get(String key) {
    Method method = mapping.get(key);
    try {
      if(method != null) {
        return method.invoke(bean);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }
}
