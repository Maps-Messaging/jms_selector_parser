# Extensible JMS Selector Parser
JMS Selector parser, this is a 2 pass parser that compiles the selector to the simplest form for faster execution.



# pom.xml setup

Add the repository configuration into the pom.xml
``` xml
<repository>
  <id>mapsmessaging.io</id>
  <name>artifactory-releases</name>
  <url>http://repo.mapsmessaging.io:8081/artifactory/mapsmessaging-mvn-prod</url>
</repository>
```    

Then include the dependency
``` xml
 <dependencies>    
    <dependency>
      <groupId>io.mapsmessaging</groupId>
      <artifactId>Extensible_JMS_Selector_Parser</artifactId>
      <version>1.0.0</version>
    </dependency>
 </dependencies>    
```    
    



[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Maps-Messaging_jms_selector&metric=alert_status)](https://sonarcloud.io/dashboard?id=Maps-Messaging_jms_selector)
