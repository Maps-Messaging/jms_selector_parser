package io.mapsmessaging.selector.operators.functions.ml;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.Operation;

import java.util.List;

public abstract class AbstractModelOperations extends Operation {
  protected final List<String> identity;
  protected final String modelName;
  protected boolean isModelTrained;

  protected AbstractModelOperations(String modelName, List<String> identity) {
    this.identity = identity;
    this.modelName = modelName;
    isModelTrained = false;
  }

  protected double[] evaluateList(IdentifierResolver resolver) throws ParseException {
    double[] dataset = new double[identity.size()];
    for (int x = 0; x < identity.size(); x++) {
      Number val = evaluateToNumber(resolver.get(identity.get(x)), resolver);
      if (val != null) {
        dataset[x] = val.doubleValue();
      } else {
        dataset[x] = Double.NaN;
      }
    }
    return dataset;
  }

  @Override
  public Object compile() {
    return this;
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder(modelName);
    for(String s : identity) {
      sb.append(", ").append(s);
    }
    return sb.toString();
  }
}

