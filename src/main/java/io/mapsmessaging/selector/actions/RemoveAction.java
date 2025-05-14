/*
 *
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *  Copyright [ 2024 - 2025.  ] [Maps Messaging B.V.]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package io.mapsmessaging.selector.actions;

import io.mapsmessaging.selector.IdentifierMutator;
import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;

public class RemoveAction extends Action {

  private final Object lhs;

  public RemoveAction(Object lhs) {
    this.lhs = lhs;
  }

  public Object compile() {
    return this;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object lookup = evaluate(lhs, resolver);
    if (resolver instanceof IdentifierMutator && lookup != null) {
      if (lookup instanceof String) {
        return ((IdentifierMutator) resolver).remove((String) lookup);
      }
      return ((IdentifierMutator) resolver).remove(lookup.toString());
    }
    return false;
  }

  public String toString() {
    return ("REMOVE (" + lhs.toString() + ")");
  }

  @Override
  public boolean equals(Object test) {
    if (test instanceof RemoveAction) {
      return (lhs.equals(((RemoveAction) test).lhs));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return lhs.hashCode();
  }
}
