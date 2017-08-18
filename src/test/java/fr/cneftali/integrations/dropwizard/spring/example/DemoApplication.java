package fr.cneftali.integrations.dropwizard.spring.example;

import fr.cneftali.integrations.dropwizard.spring.AbstractSpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = {"fr.cneftali.integrations.dropwizard.spring.example.health",
        "fr.cneftali.integrations.dropwizard.spring.example.resources"})
public class DemoApplication extends AbstractSpringApplication<DemoConfiguration> {


    public static void main(final String[] args) throws Exception {
        new DemoApplication().run(args);
    }
}