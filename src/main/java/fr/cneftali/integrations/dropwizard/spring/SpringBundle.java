package fr.cneftali.integrations.dropwizard.spring;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fr.cneftali.integrations.dropwizard.spring.servlet.SpringContextLoaderListener;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.ws.rs.Path;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Slf4j
public class SpringBundle<T extends Configuration> implements ConfiguredBundle<T> {

    public static final String APPLICATION_NAME_BEAN_NAME = "dwApplicationName";
    public static final String CONFIGURATION_BEAN_NAME = "dwConf";
    public static final String ENVIRONMENT_BEAN_NAME = "dwEnv";
    public static final String OBJECT_MAPPER_BEAN_NAME = "dwObjectMapper";

    private final String applicationName;
    private final List<Class<?>> annotatedClasses;
    private final AnnotationConfigWebApplicationContext context;

    /**
     * Creates a new KiwiSpringBundle to automatically initialize Dropwizard {@link Environment}
     * <p/>
     *
     * @param applicationName
     * @param annotatedClasses annotated classes,
     * e.g. {@link org.springframework.context.annotation.Configuration @Configuration} classes
     */
    public SpringBundle(final String applicationName, final Class<?>... annotatedClasses) {
        this.context = new AnnotationConfigWebApplicationContext();
        this.applicationName = requireNonNull(applicationName);
        if (annotatedClasses != null) {
            this.annotatedClasses = Arrays.stream(annotatedClasses)
                                          .collect(Collectors.toList());
        } else {
            this.annotatedClasses = Lists.newArrayList();
        }
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        final AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();
        parent.registerShutdownHook();
        parent.refresh();
        parent.getBeanFactory()
              .registerSingleton(APPLICATION_NAME_BEAN_NAME, applicationName);
        // Register Dropwizard Configuration as a Bean Spring.
        registerConfiguration(configuration, parent);

        // Register the Dropwizard environment
        registerEnvironment(environment, parent);

        // Register Dropwizard ObjectMapper as a Bean Spring.
        registerObjectMapper(environment.getObjectMapper(), parent);

        // Register Dropwizard Metrics as a Bean Spring.
        registerMetrics(environment, parent);

        this.context.setParent(parent);
        this.context.registerShutdownHook();
        registerModules(configuration, context);

        // Initialize Dropwizard environment
        registerManaged(environment, context);
        registerLifecycle(environment,context);
        registerServerLifecycleListeners(environment, context);
        registerTasks(environment, context);
        registerHealthChecks(environment, context);
        registerProviders(environment, context);
        registerResources(environment, context);
        registerDynamicFeature(environment, context);
        registerBinder(environment, context);
        environment.servlets().addServletListeners(new SpringContextLoaderListener(context));
    }


    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        // nothing doing
    }

    public ConfigurableWebApplicationContext getContext() {
        return context;
    }


    // ~ Dropwizard Environment initialization methods -----------------------------------------------------------------

    /**
     * Register {@link Managed}s in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerManaged(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeansOfType(Managed.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.lifecycle()
                              .manage(map.getValue());
                   log.info("Registering  managed: " + map.getValue()
                                                          .getClass()
                                                          .getName());
               });
    }


    /**
     * Register {@link LifeCycle}s in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerLifecycle(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeansOfType(LifeCycle.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.lifecycle()
                              .manage(map.getValue());
                   log.info("Registering lifeCycle: " + map.getValue()
                                                           .getClass()
                                                           .getName());
               });
    }


    /**
     * Register {@link ServerLifecycleListener}s in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerServerLifecycleListeners(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeansOfType(ServerLifecycleListener.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.lifecycle()
                              .addServerLifecycleListener(map.getValue());
                   log.info("Registering serverLifecycleListener: " + map.getValue()
                                                                         .getClass()
                                                                         .getName());
               });
    }


    /**
     * Register {@link Task}s in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerTasks(final Environment environment, final ConfigurableApplicationContext context) {
        context.getBeansOfType(Task.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.admin()
                              .addTask(map.getValue());
                   log.info("Registering task: " + map.getValue()
                                                      .getClass()
                                                      .getName());
               });
    }


    /**
     * Register {@link DynamicFeature}s in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerBinder(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeansOfType(AbstractBinder.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.jersey()
                              .register(map.getValue());
                   log.info("Registering Binder: " + map.getValue()
                                                        .getClass()
                                                        .getName());
               });

    }

    /**
     * Register {@link DynamicFeature}s in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerDynamicFeature(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeansOfType(DynamicFeature.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.jersey()
                              .register(map.getValue());
                   log.info("Registering DynamicFeature: " + map.getValue()
                                                                .getClass()
                                                                .getName());
               });

    }

    /**
     * Register {@link HealthCheck}s in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerHealthChecks(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeansOfType(HealthCheck.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.healthChecks()
                              .register(map.getKey(),
                                        map.getValue());
                   log.info("Registering healthCheck: " + map.getValue()
                                                             .getClass()
                                                             .getName());
               });
    }

    /**
     * Register objects annotated with {@link Provider} in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerProviders(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeansWithAnnotation(Provider.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.jersey()
                              .register(map.getValue());
                   log.info("Registering provider: " + map.getValue()
                                                          .getClass()
                                                          .getName());
               });
    }


    /**
     * Register resources annotated with {@link Path} in Dropwizard {@link Environment} from Spring application context.
     *
     * @param environment the Dropwizard environment
     * @param context     the Spring application context
     */
    private void registerResources(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeansWithAnnotation(Path.class)
               .entrySet()
               .stream()
               .forEach(map -> {
                   environment.jersey().register(map.getValue());
                   log.info("Registering resource: " + map.getValue()
                                                          .getClass()
                                                          .getName());
               });
    }

    /**
     * Register Dropwizard {@link io.dropwizard.Configuration} as a Bean Spring.
     *
     * @param configuration Dropwizard {@link io.dropwizard.Configuration}
     * @param context       spring application context
     */
    private void registerConfiguration(final T configuration, final ConfigurableWebApplicationContext context) {
        context.getBeanFactory()
               .registerSingleton(CONFIGURATION_BEAN_NAME,
                                  configuration);
        log.info("Registering Dropwizard Configuration under name : {}", CONFIGURATION_BEAN_NAME);
    }


    /**
     * Register Dropwizard {@link Environment} as a Bean Spring.
     *
     * @param environment Dropwizard {@link Environment}
     * @param context     Spring application context
     */
    private void registerEnvironment(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeanFactory()
               .registerSingleton(ENVIRONMENT_BEAN_NAME,
                                  environment);
        log.info("Registering Dropwizard Profile under name : {}", ENVIRONMENT_BEAN_NAME);
    }

    private void registerObjectMapper(final ObjectMapper objectMapper, final ConfigurableWebApplicationContext context) {
        context.getBeanFactory()
               .registerSingleton(OBJECT_MAPPER_BEAN_NAME,
                                  objectMapper);
        log.info("Registering Dropwizard ObjectMapper under name : {}", OBJECT_MAPPER_BEAN_NAME);
    }

    private void registerMetrics(final Environment environment, final ConfigurableWebApplicationContext context) {
        context.getBeanFactory()
               .registerSingleton(MetricRegistry.class.getSimpleName(), environment.metrics());
        log.info("Registering Dropwizard MetricRegistry under name : {}", MetricRegistry.class.getSimpleName());
    }

    private void registerModules(final T configuration, final AnnotationConfigWebApplicationContext context) {
        context.register(annotatedClasses.toArray(new Class<?>[annotatedClasses.size()]));
        context.refresh();
    }
}
