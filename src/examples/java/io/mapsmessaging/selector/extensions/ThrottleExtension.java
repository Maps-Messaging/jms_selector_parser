/*
 *
 *   Copyright [ 2020 - 2021 ] [Matthew Buckton]
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

import java.util.List;
import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import java.util.concurrent.atomic.AtomicLong;

public class ThrottleExtension implements ParserExtension {

  private long nextEpoch;
  private final AtomicLong count;
  private final int limit;
  private final int epoch;

  public ThrottleExtension(){
    count = new AtomicLong(0);
    nextEpoch = 0;
    limit = 0;
    epoch = 0;
  }

  public ThrottleExtension(List<String> arguments){
    count = new AtomicLong(0);
    limit = Integer.parseInt(arguments.get(0));
    if(arguments.size() > 1){
      epoch = Integer.parseInt(arguments.get(1));
    }
    else{
      epoch = 60000;
    }
    nextEpoch = System.currentTimeMillis()+ epoch;
  }

  @Override
  public ParserExtension createInstance(List<String> arguments) throws ParseException {
    return new ThrottleExtension(arguments);
  }

  @Override
  public Object parse(IdentifierResolver resolver) {
    if(System.currentTimeMillis() > nextEpoch){
      count.set(0);
      nextEpoch = System.currentTimeMillis() + epoch;
    }

    return count.incrementAndGet() <= limit;
  }

  @Override
  public String getName() {
    return "throttle";
  }

  @Override
  public String getDescription() {
    return "If the number of calls exceeds the configured number than it will return false till the time has expired";
  }
}
