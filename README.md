# JMS Selector Module

The JMS Selector Module, an integral part of the MapsMessaging ecosystem, is a versatile tool designed for filtering objects using JMS Selector strings. Its functionality extends beyond the MapsMessaging server, making it a valuable asset for any application requiring advanced filtering capabilities.

## Introduction
This module is not just for MapsMessaging; it's a universal solution for implementing JMS Selector based filtering in various contexts. It's particularly useful for developers in IoT and beyond, looking for a robust method to filter complex data structures.

## Standalone Use and Extensibility
While originally developed for the MapsMessaging server, this module stands on its own as a powerful tool. It's easily extendable for filtering complex objects, providing a flexible solution adaptable to various requirements.
Any object can be filtered using the module, if it can be converted to one of the currently supported types:
- Map<String, Object>
- JSONObject
- Java Bean
- Any object that implements the IdentifierMutator interface

For more advanced or complex object than by implementing a IdentifierMutator that has knowledge of the object can be filtered.

## Installation and Setup
Integrate the JMS Selector Module into your project by adding this dependency to your `pom.xml`:

``` xml
<!-- JMS Selector logic module -->
<dependency>
  <groupId>io.mapsmessaging</groupId>
  <artifactId>Extensible_JMS_Selector_Parser</artifactId>
  <version>1.1.12</version>
</dependency>
```

## Performance

### High-Performance Design
The JMS Selector Module is engineered for high performance, ensuring efficient parsing and filtering operations even in demanding environments. Performance is a key consideration in our design and implementation.

### Benchmarking
To objectively measure performance, we have employed Java Microbenchmark Harness (JMH) tests. These tests demonstrate the module's efficiency and speed under various scenarios.

#### Reference to Test Code
For transparency and to enable users to verify performance in their own environments, we have made our JMH test code available.
You can find the tests [ParallelStreamJMH](https://github.com/Maps-Messaging/jms_selector_parser/blob/main/src/test/java/io/mapsmessaging/selector/ParallelStreamJMH.java) and run them to see how the module performs in your specific setup.



## ML Model Capabilities in Messaging Server Context

This document outlines the supported machine learning models available for use within the MapsMessaging server, along with whether they can be trained from streaming data or require a pre-trained model.

| Algorithm             | Learns From Stream | Requires Pre-trained Model | Description                                                                                                                                                                        |
| --------------------- | ------------------ | -------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `TensorFlow`          | ❌                  | ✅                          | External model (e.g., TensorFlow SavedModel) for classification or regression using external inference engines. Suitable for advanced filtering or prediction in stream pipelines. |
| `k-means`             | ✅                  | ❌                          | Unsupervised clustering of messages based on feature similarity. Can be trained from raw stream data.                                                                              |
| `g-means`             | ✅                  | ❌                          | Extended K-Means that automatically determines the number of clusters. Suitable for exploratory stream clustering.                                                                 |
| `x-means`             | ✅                  | ❌                          | Advanced variant of K-Means that adjusts the cluster count based on BIC. Useful for dynamic topic-based message grouping.                                                          |
| `k-means_lloyd`       | ✅                  | ❌                          | Traditional Lloyd's algorithm for K-Means. Best used when the number of clusters is known ahead of time.                                                                           |
| `linear_regression`   | ❌                  | ✅                          | Predicts continuous values from labeled features in the stream. Requires labeled data and a pre-trained model.                                                                     |
| `ols`                 | ❌                  | ✅                          | Ordinary Least Squares regression; a basic linear model for numerical prediction. Requires labeled data.                                                                           |
| `ridge`               | ❌                  | ✅                          | Linear regression with L2 regularization, useful for avoiding overfitting in message trend prediction.                                                                             |
| `lasso`               | ❌                  | ✅                          | Linear regression with L1 regularization, used for sparse feature selection in stream analysis.                                                                                    |
| `decision_tree`       | ❌                  | ✅                          | Supervised classifier for routing or categorizing messages. Requires labeled training data and model.                                                                              |
| `naive_bayes`         | ❌                  | ✅                          | Probabilistic classifier useful for low-latency message classification. Pre-trained on labeled data.                                                                               |
| `hierarchical`        | ❌                  | ❌                          | Offline clustering only. Cannot be used in streaming. Generates static groupings for historical datasets.                                                                          |
| `pca`                 | ✅                  | ❌                          | Principal Component Analysis for dimensionality reduction. Useful for pre-processing stream features.                                                                              |
| `pca_fit`             | ✅                  | ❌                          | PCA using covariance matrix; more sensitive to scaling. Used for static feature extraction.                                                                                        |
| `pca_cor`             | ✅                  | ❌                          | PCA using correlation matrix; better for mixed-scale data. Suitable for visualization and compression.                                                                             |
| `random_forest`       | ❌                  | ✅                          | Ensemble classifier ideal for high-accuracy message filtering. Requires labeled training and model persistence.                                                                    |
| `Logistic_regression` | ❌                  | ✅                          | Binary or multi-class classifier for event labeling in the stream. Requires pre-trained model.                                                                                     |
| `mlp`                 | ❌                  | ✅                          | Multi-layer perceptron; a neural network classifier. Good for nonlinear message classification. Requires model.                                                                    |
| `qda`                 | ❌                  | ✅                          | Quadratic Discriminant Analysis for classifying Gaussian-distributed message features. Requires training.                                                                          |
| `lda`                 | ❌                  | ✅                          | Linear Discriminant Analysis; useful when message classes are linearly separable. Requires labels.                                                                                 |
| `isolation_forest`    | ✅                  | ❌                          | Unsupervised anomaly detection for outlier detection in streams. Trains on normal data only.                                                                                       |
| `knn`                 | ❌                  | ✅                          | K-Nearest Neighbors classifier. Stores raw labeled data for inference. Suitable for similarity-based classification.                                                               |
| `model_exists`        | ❌                  | ✅                          | Returns `true` or `false` based on whether a model is available for use. Useful in selector expressions.                                                                           |

**Notes:**

* "Learns From Stream" ✅ means it can be trained directly from incoming feature data.
* "Requires Pre-trained Model" ✅ means it must be trained offline and loaded at runtime.
* This table helps guide filter logic, model preparation, and expression validation in streaming contexts.



## Usage and Examples
### General Filtering
Here's how you can use the module in a general context:


## Filtering java collections using Streams

With the addition of Streams in the Java collection API, filtering objects within these become trivial.
The example below creates a list of address and then filters this list using the stream() functions.

```java
public class StreamExample {

  private static final int LIST_SIZE = 10000;
  private final static List<Address> addressList =buildList();
  
  @Test
  public void simpleStream1() throws ParseException {
    ParserExecutor executor = SelectorParser.compile("state = 'Alaska'");
    long alaskanAddresses = addressList.stream().filter(executor::evaluate).count();
    System.err.println("Alaskan : "+alaskanAddresses);
  }

  @Test
  public void simpleStream2() throws ParseException {
    ParserExecutor executor = SelectorParser.compile("state IN ('Alaska', 'Hawaii')");
    long alaskanAddresses = addressList.stream().filter(executor::evaluate).count();
    System.err.println("Alaskan OR Hawaii : "+alaskanAddresses);
  }

  @Test
  public void simpleParallel() throws ParseException {
    ParserExecutor executor = SelectorParser.compile("state IN ('Alaska', 'Hawaii')");
    long alaskanAddresses = addressList.parallelStream().filter(executor::evaluate).count();
    System.err.println("Alaskan OR Hawaii : "+alaskanAddresses);
  }

  private static List<Address> buildList(){
    List<Address> addressList = new ArrayList<>();
    Faker faker = new Faker();
    for (int x = 0; x < LIST_SIZE; x++) {
      addressList.add(new Address(faker.address()));
    }
    return addressList;
  }

  public static class Address{
    @Getter final String street;
    @Getter final String suburb;
    @Getter final String zipCode;
    @Getter final String state;


    public Address(com.github.javafaker.Address address) {
      this.state = address.state();
      this.street = address.streetAddress();
      this.suburb = address.city();
      this.zipCode = address.zipCode();

    }
  }
}

```

### Advanced Filtering of Complex Objects
Extend the module's capabilities to filter more complex objects:


### Filtering Beans
Here we have an object, BeanTest, that has an integer counter. We create a filter that will return true only if the counter == 10.

```java
class SelectorValidationTest {

  @Test
  void checkBeans() {
    BeanTest bean = new BeanTest();
    bean.setCounter(0);
    ParserExecutor parser = SelectorParser.compile("counter = 10");
    for (int x = 0; x < 9; x++) {
      bean.increment();
      Assertions.assertFalse(parser.evaluate(bean));
    }
    bean.increment();
    Assertions.assertTrue(parser.evaluate(bean));
  }

  // Test bean class, can be any Java class that offers get functions
  public class BeanTest {
    private int counter;

    public void increment() {
      counter++;
    }

    public int getCounter() {
      return counter;
    }

    public void setCounter(int counter) {
      this.counter = counter;
    }
  }
}
```
Please note that for Java beans the name of the key is case-sensitive, so notice that the syntax uses "counter" and not "Counter" as the key.

### Filtering JSON Objects

The parser will also detect if the object is of JSON type and will parse it accordingly.
In this example we have a JSON object that has a key "counter" and we want to filter it if the value of the state is "Alaska".

Currently, it supports org.json and org.json.simple JSON objects. If you are using other JSON obejcts then as long as they can be converted into a Map<String, Object> then they will be supported.
Otherwise, you can extend the parser to support your JSON object by adding a new Evaluator to the code base.

```java
class JsonFilteringTest {

  private final static int LIST_SIZE = 1000;

  @Test
  void simpleJsonFiltering() throws ParseException {
    JSONArray addressList = buildList();
    int alaskaCount = 0;
    int filtered = 0;
    ParserExecutor executor = SelectorParser.compile("state = 'Alaska'");
    for(int x=0;x<addressList.length();x++){
      JSONObject jsonObject = addressList.getJSONObject(x);
      if(jsonObject.get("state").equals("Alaska")){
        alaskaCount++;
      }
      if(executor.evaluate(jsonObject)){
        filtered++;
      }
    }
    Assertions.assertEquals(alaskaCount, filtered);

  }


  private static JSONArray buildList(){
    JSONArray addressList = new JSONArray();
    Faker faker = new Faker();
    for (int x = 0; x < LIST_SIZE; x++) {
      JSONObject jsonObject = new JSONObject();
      Address address = new Address(faker.address());
      jsonObject.put("street", address.getStreet());
      jsonObject.put("suburb", address.getSuburb());
      jsonObject.put("zipCode", address.getZipCode());
      jsonObject.put("state", address.getState());
      addressList.put(jsonObject);
    }
    return addressList;
  }

  @Data
  public static class Address{
    final String street;
    final String suburb;
    final String zipCode;
    final String state;

    public Address(com.github.javafaker.Address address) {
      this.state = address.state();
      this.street = address.streetAddress();
      this.suburb = address.city();
      this.zipCode = address.zipCode();

    }
  }
}
```


### Example: Filtering with a Trained ML Model

This example demonstrates filtering a stream of events using a trained `random_forest` model to classify input features.

```java
@Test
public void filterUsingMLModel() throws ParseException {
  ParserExecutor executor = SelectorParser.compile("random_forest(classify, model.arff, CO₂, temperature, humidity) = 'High'");
  long highRiskCount = messageStream.stream().filter(executor::evaluate).count();
  System.out.println("High risk messages: " + highRiskCount);
}
```

* The model (`model.arff`) must be pre-trained and available.
* The fields `temperature`, `humidity`, and `CO₂` are input features used in classification.
* The result is compared to `'High'` for thresholding or routing.

This pattern supports real-time filtering and routing based on live data characteristics.


## Compatibility
The module is designed for broad compatibility and can be seamlessly integrated into various systems, not limited to IoT messaging.

## Contributing
Contributions are welcome to enhance the module's functionality and applicability.

## Support
For support and queries, please reach out through our [support channel](https://www.mapsmessaging.io/support).

---
For further details about the MapsMessaging ecosystem, visit [our main project page](https://www.mapsmessaging.io/).



[![Build status](https://badge.buildkite.com/f583bc25c29d7d49b1d4566b07f06eda241d3de9c2cff056c0.svg)](https://buildkite.com/mapsmessaging/010-jms-selector-library-snapshot-build)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Maps-Messaging_jms_selector&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Maps-Messaging_jms_selector)


