package fr.cneftali.integrations.dropwizard.spring;

import org.hibernate.validator.parameternameprovider.ReflectionParameterNameProvider;
import org.springframework.util.ReflectionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adds jersey support to parameter name discovery in hibernate validator.
 *
 * <p>This provider will behave like the hibernate-provided {@link ReflectionParameterNameProvider} except when a
 * method parameter is annotated with a jersey parameter annotation, like {@link QueryParam}. If a jersey parameter
 * annotation is present the value of the annotation is used as the parameter name.</p>
 */
public class ParameterNameProvider extends ReflectionParameterNameProvider {

    @Override
    public List<String> getParameterNames(final Method method) {
        Class jaxrsClassDef = getJaxRSClass(method);
        Method jaxrsMethod = null;
        if (jaxrsClassDef != null) {
            jaxrsMethod = ReflectionUtils.findMethod(jaxrsClassDef, method.getName(), method.getParameterTypes());
        }
        return getParameterName(jaxrsMethod == null ? method : jaxrsMethod);
    }

    private Class getJaxRSClass(final Method method) {
        Class jaxrsClassDef = null;
        for (final Class c : method.getDeclaringClass().getInterfaces()) {
           if (c.isAnnotationPresent(Path.class)) {
               jaxrsClassDef = c;
               break;
           }
        }
        return jaxrsClassDef;
    }

    private List<String> getParameterName(final Method method) {
        final Parameter[] parameters = method.getParameters();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<String> names = new ArrayList<>(parameterAnnotations.length);
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            String name = getParameterNameFromAnnotations(annotations).orElse(parameters[i].getName());
            names.add(name);
        }
        return names;
    }

    /**
     * Derives member's name and type from it's annotations
     */
    public static Optional<String> getParameterNameFromAnnotations(final Annotation[] memberAnnotations) {
        for (final Annotation a : memberAnnotations) {
            if (a instanceof QueryParam) {
                return Optional.of("The query param '" + ((QueryParam) a).value() + "'");
            } else if (a instanceof PathParam) {
                return Optional.of("The path param '" + ((PathParam) a).value() + "'");
            } else if (a instanceof HeaderParam) {
                return Optional.of("The header '" + ((HeaderParam) a).value() + "'");
            } else if (a instanceof CookieParam) {
                return Optional.of("The cookie '" + ((CookieParam) a).value() + "'");
            } else if (a instanceof FormParam) {
                return Optional.of("The form field '" + ((FormParam) a).value() + "'");
            } else if (a instanceof Context) {
                return Optional.of("context");
            } else if (a instanceof MatrixParam) {
                return Optional.of("The matrix param '" + ((MatrixParam) a).value() + "'");
            }
        }

        return Optional.empty();
    }
}