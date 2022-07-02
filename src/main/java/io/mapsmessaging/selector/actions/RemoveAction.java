package io.mapsmessaging.selector.actions;

import io.mapsmessaging.selector.IdentifierMutator;
import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;

public class RemoveAction extends Action {

  private final Object lhs;

  public RemoveAction(Object lhs){
    this.lhs = lhs;
  }

  public Object compile(){
    return this;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object lookup = evaluate(lhs, resolver);
    if(resolver instanceof IdentifierMutator && lookup != null) {
      if (lookup instanceof String) {
        return ((IdentifierMutator) resolver).remove((String) lookup);
      }
      return ((IdentifierMutator) resolver).remove(lookup.toString());
    }
    return false;
  }

  public String toString(){
    return ("REMOVE (" + lhs.toString() + ")");
  }

  @Override
  public boolean equals(Object test){
    if(test instanceof RemoveAction){
      return (lhs.equals(((RemoveAction) test).lhs));
    }
    return false;
  }

  @Override
  public int hashCode(){
    return lhs.hashCode();
  }
}
