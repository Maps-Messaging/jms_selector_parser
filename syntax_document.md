
# JavaCC Grammar Functions Documentation

## Overview

This document describes the various functions and operators supported by the JavaCC grammar for JMS message selectors, including extensions and variances from the standard JMS selectors. Each function is explained along with examples of its usage.

## Functions and Operators

### K-means Clustering

- **Description**: Performs K-means clustering on the provided data. *(Extension)*
- **Usage**: `K-means_clustering(modelName, param1, param2, ...)`
- **Example**:
  ```sql
  K-means_clustering(modelName, param1, param2, ...)
  ```

### Linear Regression

- **Description**: Performs linear regression analysis. *(Extension)*
- **Usage**: `linear_regression(param1, param2, ...)`
- **Example**:
  ```sql
  linear_regression(modelName, param1, param2, ...)
  ```

### Decision Tree

- **Description**: Builds a decision tree for classification or regression. *(Extension)*
- **Usage**: `decision_tree(param1, param2, ...)`
- **Example**:
  ```sql
  decision_tree(modelName, param1, param2, ...)
  ```

### Naive Bayes

- **Description**: Performs classification using the Naive Bayes algorithm. *(Extension)*
- **Usage**: `naive_bayes(modelName, param1, param2, ...)`
- **Example**:
  ```sql
  naive_bayes(modelName, param1, param2, ...)
  ```

### Hierarchical Clustering

- **Description**: Performs hierarchical clustering. *(Extension)*
- **Usage**: `hierarchical_clustering(modelName, param1, param2, ...)`
- **Example**:
  ```sql
  hierarchical_clustering(modelName, param1, param2, ...)
  ```

### PCA (Principal Component Analysis)

- **Description**: Performs Principal Component Analysis. *(Extension)*
- **Usage**: `pca(modelName, param1, param2, ...)`
- **Example**:
  ```sql
  pca(modelName, param1, param2, ...)
  ```

### Model Exists

- **Description**: Checks if a model exists. *(Extension)*
- **Usage**: `model_exists(modelName)`
- **Example**:
```sql
  model_exists("linearModel")
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
