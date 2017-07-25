DropWizard Spring Integration
=============================

Introduction
------------

[Dropwizard](http://www.dropwizard.io) is a Java framework for developing ops-friendly, high-performance, RESTful web services.

[Spring](http://projects.spring.io/spring-framework/) is the most popular application development framework for enterprise Java™.

This project provide a simple method for integrating Spring with Dropwizard.


Usage
-----
Just extend `AbstractSpringApplication` class and create your configuration class that extends `Configuration`

```java
@lombok.Getter
@lombok.Setter
public class DemoConfiguration extends KiwiConfiguration {
    @com.fasterxml.jackson.annotation.JsonProperty("myConfigurationElement")
    private String myConfigurationElement = "default value";
}
```
```java
@org.springframework.context.annotation.Configuration
public class DemoApplication extends fr.cneftali.integrations.dropwizard.spring.AbstractSpringApplication<DemoConfiguration> {
     
     public DemoApplication() {
         super();
     }
     
     public static void main(final String[] args) throws Exception {
       new DemoApplication().run(args);
     }
}
```