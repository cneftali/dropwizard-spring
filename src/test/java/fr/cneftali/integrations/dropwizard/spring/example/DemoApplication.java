package fr.cneftali.integrations.dropwizard.spring.example;

import fr.cneftali.integrations.dropwizard.spring.AbstractSpringApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoApplication extends AbstractSpringApplication<DemoConfiguration> {
     
     public static void main(final String[] args) throws Exception {
       new DemoApplication().run(args);
     }
}