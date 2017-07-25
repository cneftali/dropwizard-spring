package fr.cneftali.integrations.dropwizard.spring;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public interface SpringApplication<C extends Configuration> {

    /**
     * Callback method when the Dropwizard application just finished its initialization.
     *
     * @param bootstrap
     */
    default void onInitialize(final Bootstrap<C> bootstrap) {
    }

    /**
     * Callback method before run the Dropwizard application.
     *
     * @param configuration
     * @param environment
     * @param context
     */
    default void configure(final C configuration, final Environment environment, final ConfigurableWebApplicationContext context) {
    }
}