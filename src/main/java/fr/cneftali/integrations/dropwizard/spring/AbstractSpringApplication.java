package fr.cneftali.integrations.dropwizard.spring;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.NonEmptyStringParamUnwrapper;
import io.dropwizard.jersey.validation.ParamValidatorUnwrapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.validation.BaseValidator;
import lombok.extern.slf4j.Slf4j;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Slf4j
public abstract class AbstractSpringApplication<C extends Configuration> extends Application<C> implements SpringApplication {

    private final SpringBundle<C> springBundle;

    public AbstractSpringApplication(final Class<?>... annotatedClasses) {
        if (annotatedClasses != null) {
            this.springBundle = new SpringBundle<>(getName(), annotatedClasses);
        } else if (getClass().isAnnotationPresent(org.springframework.context.annotation.Configuration.class)) {
            this.springBundle = new SpringBundle<>(getName(), getClass());
        } else {
            this.springBundle = new SpringBundle<>(getName());
        }
    }

    public AbstractSpringApplication() {
        this(null);
    }

    @Override
    public final void run(final C configuration, final Environment environment) throws Exception {
        configure(configuration, environment, springBundle.getContext());
    }

    @Override
    public final void initialize(final Bootstrap<C> bootstrap) {
        initObjectMapper(bootstrap);
        initBeanValidation(bootstrap);
        bootstrap.addBundle(springBundle);
        onInitialize(bootstrap);
    }

    private void initBeanValidation(final Bootstrap<C> bootstrap) {
        bootstrap.setValidatorFactory(BaseValidator.newConfiguration()
                                                   .parameterNameProvider(new ParameterNameProvider())
                                                   .addValidatedValueHandler(new NonEmptyStringParamUnwrapper())
                                                   .addValidatedValueHandler(new ParamValidatorUnwrapper())
                                                   .buildValidatorFactory());

    }

    private void initObjectMapper(final Bootstrap<C> bootstrap) {
        bootstrap.setObjectMapper(Jackson.newObjectMapper()
                                         .enable(INDENT_OUTPUT)
                                         .disable(WRITE_DATES_AS_TIMESTAMPS)
                                         .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                                         .setDateFormat(new ISO8601DateFormat())
                                         .setSerializationInclusion(NON_ABSENT));
    }
}
