# Extensible JMS Selector Parser
JMS Selector parser, this is a 2 pass parser that compiles the selector to the simplest form for faster execution.



## pom.xml setup

Add the repository configuration into the pom.xml
``` xml
    <!-- MapsMessaging jfrog server -->
    <repository>
      <id>mapsmessaging.io</id>
      <name>artifactory-releases</name>
      <url>https://mapsmessaging.jfrog.io/artifactory/mapsmessaging-mvn-prod</url>
    </repository>
```    

Then include the dependency
``` xml
    <!-- JMS Selector logic module -->
     <dependencies>    
        <dependency>
          <groupId>io.mapsmessaging</groupId>
          <artifactId>Extensible_JMS_Selector_Parser</artifactId>
          <version>1.0.0</version>
        </dependency>
     </dependencies>    
```    
    
## Usage

To build a ParserExecutor object you simply supply a valid JMS Selector string (As per section 3.8.1 of the JMS 2.0 standard found [here](https://download.oracle.com/otndocs/jcp/jms-2_0_rev_a-mrel-eval-spec/index.html))
```java
  String selector = "currency IN ('aud', 'usd', 'jpy')"
  ParserExecutor filter = SelectorParser.compile(selector);
```
The SelectorParser will then compile the selector into a ParserExecutor. Any arithmetic operations that can be performed are done as part of this compilation as is any boolean operations.

The resultant ParserExecutor is a thread safe filter that can be used with any key/value object that implements [IdentifierResolver.java](src/main/java/io/mapsmessaging/selector/IdentifierResolver.java).  


## Extending the Selector

The selector syntax has an additional verb that is not found in the JMS Selector syntax called <b>extension</b> that can be used to add further filtering logic that you may need.

The library comes with a built-in JSON extension that can be used to filter JSON objects that are supplied as byte[].

To extend the selector simply 

* Create a new filter extension by extending the interface ParserExtension.java
* Update the java services file found in META-INF.servers
* Update your selector string to reference it

The standard syntax for the extension is as follows

```text
Identifier|value = extension ('<extension name>', 'argument 0', 'argument 1', ,,, 'argument n');
```

The extension verb uses the first parameter as the name to look up, this name is the name returned by your extensions getName() function.
Once the selector has located the extension it then creates a new instance and passes the arguments to it. Then on each subsequent <b>ParserExecutor.evaluate</b> call the extensions own evaluate is called with an Identifier to use as part of the lookup


## Example Extension

For example, lets increment a simple counter, all it does is increment a counter whenever it is called

```java

public class CounterExtension implements ParserExtension {

  private AtomicLong counter = new AtomicLong(0);

  @Override
  public ParserExtension createInstance(List<String> arguments)  {
    return new CounterExtension();
  }

  @Override
  public Object parse(IdentifierResolver resolver) {
    return counter.getAndIncrement();
  }

  @Override
  public String getName() {
    return "counter";
  }

  @Override
  public String getDescription() {
    return "Simple parse counter, increments every call";
  }
}


```

Update the configuration file META-INF.servers/io.mapsmessaging.selector.extensions.ParserExtension and add the following

```properties
io.mapsmessaging.selector.extensions.CounterExtension
```

Now to use it in the syntax
```java
public class Example {
  void simpleCounter() throws ParseException {
    String selector = "10 = extension ('counter', '')";
    ParserExecutor parserExecutor = SelectorParser.compile(selector);

    // This should fail the counter is less then 10
    for (int x = 0; x < 10; x++) {
      Assertions.assertFalse(parserExecutor.evaluate(key -> "Hi"));
    }
    // This should work since the counter is in fact 10
    Assertions.assertTrue(parserExecutor.evaluate(key -> "Hi"));
  }
}
```



Examples can be found [here](src/examples/java/io/mapsmessaging/selector) 



[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Maps-Messaging_jms_selector&metric=alert_status)](https://sonarcloud.io/dashboard?id=Maps-Messaging_jms_selector)
