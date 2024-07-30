package io.mapsmessaging.selector.operators.functions.ml.impl.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.Operation;
import io.mapsmessaging.selector.operators.functions.MLFunction;

public class ModelExistFunction extends Operation {

  private final String modelName;

  public ModelExistFunction(String modelName){
    this.modelName = modelName;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException{
    try {
      return MLFunction.getModelStore().modelExists(modelName);
    } catch (Exception e) {
      ParseException ex = new ParseException(e.getMessage());
      ex.initCause(e);
      throw ex;
    }
  }

  public String toString(){
    return "model_exist(" + modelName + ")";
  }


  @Override
  public Object compile(){
    return this;
  }
}
