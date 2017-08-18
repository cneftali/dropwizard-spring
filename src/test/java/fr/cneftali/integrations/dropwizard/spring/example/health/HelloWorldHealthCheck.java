package fr.cneftali.integrations.dropwizard.spring.example.health;

import com.codahale.metrics.health.HealthCheck;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}