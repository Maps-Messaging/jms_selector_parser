
# JavaCC Grammar Functions Documentation

## Overview

This document describes the various functions and operators supported by the JavaCC grammar for JMS message selectors, including extensions and variances from the standard JMS selectors. Each function is explained along with examples of its usage.

## Functions and Operators

### K-means Clustering

- **Description**: Performs K-means clustering on the provided data. *(Extension)*
- **Usage**: `K-means_clustering(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `distance`: Returns the distance of the instance from the cluster centroid.
  - `clusterLabel`: Returns the cluster label of the instance.
  - `centroid[idx]`: Returns the value of the `idx`th dimension of the cluster centroid.
  - `clusterSizes[idx]`: Returns the size of the `idx`th cluster.
  - `totalClusters`: Returns the total number of clusters.
- **Example**:
  ```sql
  K-means_clustering(distance, home_temp_model, temperature, humidity)
  ```

### Linear Regression

- **Description**: Performs linear regression analysis. *(Extension)*
- **Usage**: `linear_regression(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `predict`: Predicts the target value based on the input features.
  - `coefficients`: Returns the regression coefficients.
  - `r_squared`: Returns the R-squared value of the model.
  - `mse`: Returns the mean squared error of the model.
  - `intercept`: Returns the intercept of the regression model.
- **Example**:
  ```sql
  linear_regression(predict, temperature_model, temperature, humidity, pressure)
  ```

### Decision Tree

- **Description**: Builds a decision tree for classification or regression. *(Extension)*
- **Usage**: `decision_tree(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `classify`: Classifies the instance and returns the predicted class.
  - `classifyProb`: Returns the probability distribution over all possible classes for the instance.
- **Example**:
  ```sql
  decision_tree(classify, weather_model, temperature, humidity, pressure)
  ```

### Naive Bayes

- **Description**: Performs classification using the Naive Bayes algorithm. *(Extension)*
- **Usage**: `naive_bayes(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `classify`: Classifies the instance and returns the predicted class.
  - `classifyProb`: Returns the probability distribution over all possible classes for the instance.
- **Example**:
  ```sql
  naive_bayes(classify, email_model, word1, word2, word3)
  ```

### Hierarchical Clustering

- **Description**: Performs hierarchical clustering. *(Extension)*
- **Usage**: `hierarchical_clustering(modelName, param1, param2, ...)`
- **Operations**:
  - `dendrogram`: Returns the dendrogram of the hierarchical clustering.
  - `clusterLabel`: Returns the cluster label of the instance.
- **Example**:
  ```sql
  hierarchical_clustering(hierarchy_model, param1, param2, ...)
  ```

### PCA (Principal Component Analysis)

- **Description**: Performs Principal Component Analysis. *(Extension)*
- **Usage**: `pca(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `applypca[idx]`: Applies PCA and returns the `idx`th principal component.
  - `explainedVariance`: Returns the total explained variance.
- **Example**:
  ```sql
  pca(applypca[1], pca_model, param1, param2, ...)
  ```

### TensorFlow

- **Description**: Performs operations using a TensorFlow model. *(Extension)*
- **Usage**: `tensorflow(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `predict`: Predicts the target value based on the input features.
- **Example**:
  ```sql
  tensorflow(predict, tf_model, feature1, feature2, feature3)
  ```

### Model Exists

- **Description**: Checks if a model exists. *(Extension)*
- **Usage**: `model_exists(modelName)`
- **Example**:
  ```sql
  model_exists(linearModel)
  ```

## Logical Operators

### AND

- **Description**: Logical AND operator.
- **Usage**: `expression AND expression`
- **Example**:
  ```sql
  (x > 5) AND (y < 10)
  ```

### OR

- **Description**: Logical OR operator.
- **Usage**: `expression OR expression`
- **Example**:
  ```sql
  (x > 5) OR (y < 10)
  ```

### NOT

- **Description**: Logical NOT operator.
- **Usage**: `NOT expression`
- **Example**:
  ```sql
  NOT (x > 5)
  ```

## Comparison Operators

### BETWEEN

- **Description**: Checks if a value is between two specified values.
- **Usage**: `value BETWEEN low AND high`
- **Example**:
  ```sql
  age BETWEEN 18 AND 65
  ```

### LIKE

- **Description**: Checks if a value matches a specified pattern.
- **Usage**: `value LIKE pattern`
- **Example**:
  ```sql
  name LIKE 'John%'
  ```

### IN

- **Description**: Checks if a value is within a set of values.
- **Usage**: `value IN (value1, value2, ...)`
- **Example**:
  ```sql
  country IN ('USA', 'Canada', 'UK')
  ```

### IS NULL

- **Description**: Checks if a value is NULL.
- **Usage**: `value IS NULL`
- **Example**:
  ```sql
  middleName IS NULL
  ```

## Arithmetic Operators

### Addition (+)

- **Description**: Adds two values.
- **Usage**: `value1 + value2`
- **Example**:
  ```sql
  salary + bonus
  ```

### Subtraction (-)

- **Description**: Subtracts one value from another.
- **Usage**: `value1 - value2`
- **Example**:
  ```sql
  revenue - cost
  ```

### Multiplication (*)

- **Description**: Multiplies two values.
- **Usage**: `value1 * value2`
- **Example**:
  ```sql
  quantity * price
  ```

### Division (/)

- **Description**: Divides one value by another.
- **Usage**: `value1 / value2`
- **Example**:
  ```sql
  total / count
  ```

## Example Usage

### Parsing an Expression

To parse an expression using the `SelectorParser`, you can use the following method:

```java
SelectorParser parser = new SelectorParser("age BETWEEN 18 AND 65");
Object result = parser.evaluate(obj);
```

This document provides a basic overview and examples of the various functions and operators defined in your JavaCC grammar. For more detailed information, refer to the comments and definitions within the grammar file itself.
