package io.mapsmessaging.selector.actions;

import io.mapsmessaging.selector.IdentifierMutator;
import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;

public class SetAction extends Action {

  private final Object lhs;
  private final Object rhs;

  public SetAction(Object lhs, Object rhs){
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public Object compile(){
    return this;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object lhsLookup = evaluate(lhs, resolver);
    Object rhsLookup = evaluate(rhs, resolver);
    if(lhsLookup != null && resolver instanceof IdentifierMutator) {
      return ((IdentifierMutator)resolver).set(lhsLookup.toString(), rhsLookup);
    }
    return false;
  }

  public String toString(){
    return ("SET (" + lhs.toString() +" , "+rhs.toString() + ")");
  }

  @Override
  public boolean equals(Object test){
    if(test instanceof SetAction){
      return (lhs.equals(((SetAction) test).lhs));
    }
    return false;
  }

  @Override
  public int hashCode(){
    return lhs.hashCode();
  }
}

