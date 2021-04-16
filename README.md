# Extensible JMS Selector Parser
JMS Selector parser, this is a 2 pass parser that compiles the selector to the simplest form for faster execution.

Here are the links for the [selector usage](https://www.mapsmessaging.io/selector/usage.html) and how to [extend the selector](https://www.mapsmessaging.io/selector/extensions.html)


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


[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Maps-Messaging_jms_selector&metric=alert_status)](https://sonarcloud.io/dashboard?id=Maps-Messaging_jms_selector)
