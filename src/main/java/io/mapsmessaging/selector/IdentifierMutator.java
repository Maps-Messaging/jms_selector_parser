package io.mapsmessaging.selector;

public interface IdentifierMutator extends IdentifierResolver {

  Object remove(String key);

  Object set(String key, Object value);

}
