package fr.cneftali.integrations.dropwizard.spring.example.health;

import com.codahale.metrics.health.HealthCheck;

public class HelloWorldHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}