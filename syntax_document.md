
# JavaCC Grammar Functions Documentation

## Overview

This document describes the various functions and operators supported by the JavaCC grammar for JMS message selectors, including extensions and variances from the standard JMS selectors. Each function is explained along with examples of its usage.

## Functions and Operators

### K-means Clustering

- **Description**: Performs K-means clustering on the provided data. K-means clustering partitions data into K distinct clusters based on feature similarity.
- **Usage**: `K-means_clustering(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `distance`: Returns the distance of the instance from the nearest cluster centroid. This can be used to understand how close a data point is to the center of the cluster it belongs to.
  - `clusterLabel`: Returns the cluster label of the instance, indicating which cluster the instance belongs to.
  - `centroid[idx]`: Returns the value of the `idx`th dimension of the cluster centroid, providing insight into the cluster's center in a specific dimension.
  - `clusterSizes[idx]`: Returns the size of the `idx`th cluster, which indicates the number of instances in that cluster.
  - `totalClusters`: Returns the total number of clusters formed by the K-means algorithm.
  - `silhouettescore` : Returns the silhouette score for the clustering, which measures how similar an instance is to its own cluster compared to other clusters. This score helps evaluate the quality of the clustering.


- **Example**:
  ```sql
  K-means_clustering(distance, home_temp_model, temperature, humidity)
  ```

### Linear Regression

- **Description**: Performs linear regression analysis to predict a target variable based on one or more predictor variables. Linear regression fits a linear model to the data.
- **Usage**: `linear_regression(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `predict`: Predicts the target value based on the input features using the linear regression model.
- **Example**:
  ```sql
  linear_regression(predict, temperature_model, temperature, humidity, pressure)
  ```

### Decision Tree

- **Description**: Builds a decision tree for classification or regression. Decision trees are used to model decisions and their possible consequences, including chance event outcomes.
- **Usage**: `decision_tree(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `classify`: Classifies the instance and returns the predicted class label.
  - `classifyProb`: Returns the probability distribution over all possible classes for the instance.
- **Example**:
  ```sql
  decision_tree(classify, weather_model, temperature, humidity, pressure)
  ```

### Naive Bayes

- **Description**: Performs classification using the Naive Bayes algorithm. Naive Bayes classifiers are simple probabilistic classifiers based on applying Bayes' theorem with strong (naive) independence assumptions between the features.
- **Usage**: `naive_bayes(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `classify`: Classifies the instance and returns the predicted class label.
  - `classifyProb`: Returns the probability distribution over all possible classes for the instance.
- **Example**:
  ```sql
  naive_bayes(classify, email_model, word1, word2, word3)
  ```

### Hierarchical Clustering

- **Description**: Performs hierarchical clustering, which is a method of cluster analysis that seeks to build a hierarchy of clusters.
- **Usage**: `hierarchical_clustering(modelName, param1, param2, ...)`
- **Example**:
  ```sql
  hierarchical_clustering(hierarchy_model, param1, param2, ...)
  ```

### PCA (Principal Component Analysis)

- **Description**: Performs Principal Component Analysis (PCA) to reduce the dimensionality of the data by transforming it to a new set of variables (principal components) that are uncorrelated and that capture the maximum variance in the data.
- **Usage**: `pca(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `applypca[idx]`: Applies PCA and returns the `idx`th principal component.
  - `explainedVariance`: Returns the total explained variance by the principal components.
- **Example**:
  ```sql
  pca(applypca[1], pca_model, param1, param2, ...)
  ```

### TensorFlow

- **Description**: Performs operations using a TensorFlow model. TensorFlow is an open-source platform for machine learning.
- **Usage**: `tensorflow(operationName, modelName, param1, param2, ...)`
- **Operations**:
  - `predict`: Predicts the target value based on the input features using the TensorFlow model.
- **Example**:
  ```sql
  tensorflow(predict, tf_model, feature1, feature2, feature3)
  ```

### Model Exists

- **Description**: Checks if a model exists in the model store. This can be used to verify the presence of a pre-trained model before attempting to use it.
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
