package fr.cneftali.integrations.dropwizard.spring.example;

import com.codahale.metrics.health.HealthCheck;
import fr.cneftali.integrations.dropwizard.spring.AbstractSpringApplication;
import fr.cneftali.integrations.dropwizard.spring.example.health.HelloWorldHealthCheck;
import fr.cneftali.integrations.dropwizard.spring.example.resources.HelloWorldResource;
import fr.cneftali.integrations.dropwizard.spring.example.resources.HelloWorldResourceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoApplication extends AbstractSpringApplication<DemoConfiguration> {


    @Bean
    public HelloWorldResource resource1() {
        return new HelloWorldResourceImpl();
    }

    @Bean
    public HealthCheck healthCheck() {
        return new HelloWorldHealthCheck();
    }

    public static void main(final String[] args) throws Exception {
        new DemoApplication().run(args);
    }
}