package fr.cneftali.integrations.dropwizard.spring.servlet;

import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static java.util.Objects.requireNonNull;
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;

public class SpringContextLoaderListener implements ServletContextListener {

    private final ConfigurableWebApplicationContext context;

    public SpringContextLoaderListener(final ConfigurableWebApplicationContext context) {
        this.context = requireNonNull(context);
    }

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        sce.getServletContext().setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        context.setServletContext(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        //do nothing
    }
}