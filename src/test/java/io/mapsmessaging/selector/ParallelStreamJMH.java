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

package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class ParallelStreamJMH {

  ParserExecutor executor;
  private List<IdentityAccess> data;

  @Setup
  public void parallelStreams() throws ParseException {
    data = new ArrayList<>();
    for (int x = 0; x < 1000000; x++) {
      HashMap<String, Object> entry = new LinkedHashMap<>();
      entry.put("even", x % 2 == 0);
      data.add(entry::get);
    }
    executor = SelectorParser.compile("even = true");
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  public void compilation(Blackhole blackhole) {
    for (String selector : SelectorConformanceTest.SELECTOR_TEXT) {
      try {
        Object parser = SelectorParser.compile(selector);
        blackhole.consume(parser.toString());
      } catch (ParseException e) {
        Assertions.fail("Selector text:" + selector + " failed with exception " + e.getMessage());
      }
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  public void calculateParallelFilteredCount(Blackhole blackhole) {
    blackhole.consume(data.parallelStream()
        .filter(executor::evaluate)
        .count());
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  public void calculateNativeParallelFilteredCount(Blackhole blackhole) {
    blackhole.consume(data.parallelStream()
        .filter(resolver -> (Boolean) resolver.get("even"))
        .count());
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  public void calculateFilteredCount(Blackhole blackhole) {
    blackhole.consume(data.stream()
        .filter(executor::evaluate)
        .count());
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  public void calculateNativeFilteredCount(Blackhole blackhole) {
    blackhole.consume(data.stream()
        .filter(resolver -> (Boolean) resolver.get("even"))
        .count());
  }


  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  public void calculateFilteredAny(Blackhole blackhole) {
    blackhole.consume(data.stream().anyMatch(executor::evaluate));
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  public void timedTest(Blackhole blackhole) throws ParseException {
    for (String selector : SelectorConformanceTest.SELECTOR_TEXT) {
      blackhole.consume(SelectorParser.compile(selector));
    }
  }
}
