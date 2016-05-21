package fr.kissy.mapper.ghost;

import fr.kissy.mapper.ghost.annotation.CssSelector;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Guillaume Le Biller (<i>guillaume.lebiller@gmail.com</i>)
 * @version $Id$
 */
public class GhostBeanMapper {
    private static final Logger logger = LoggerFactory.getLogger(GhostBeanMapper.class);

    private GhostValueMapper valueMapper;
    private GhostBeanFactory beanFactory;

    protected GhostBeanMapper() {
        this.valueMapper = new GhostValueMapper(this);
        this.beanFactory = new GhostBeanFactory(valueMapper);
    }

    public <T> T readBean(final WebDriver driver, final SearchContext searchContext,
                          final Class<T> objectType, final List<String> rootCssSelectors) {
        appendCssSelectorsFromAnnotation(objectType, rootCssSelectors);
        T instance = beanFactory.createInstance(driver, searchContext, objectType, rootCssSelectors);
        processFields(driver, searchContext, objectType, instance, rootCssSelectors);
        processMethods(driver, searchContext, objectType, instance, rootCssSelectors);
        return instance;
    }

    private <T> void appendCssSelectorsFromAnnotation(final Class<T> objectType, final List<String> rootCssSelectors) {
        CssSelector annotation = objectType.getAnnotation(CssSelector.class);
        if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
            rootCssSelectors.add(annotation.value());
        }
    }

    private <T> void processFields(final WebDriver driver, final SearchContext searchContext,
                                   final Class<T> entityClass, final T instance, final List<String> cssSelectors) {
        for (Field field : entityClass.getDeclaredFields()) {
            CssSelector annotation = field.getAnnotation(CssSelector.class);
            if (annotation == null && field.getType().getAnnotation(CssSelector.class) == null) {
                continue;
            }

            Object value = valueMapper.getValue(driver, searchContext, annotation, field.getType(),
                    field.getGenericType(), cssSelectors);

            try {
                FieldUtils.writeField(field, instance, value, true);
            } catch (IllegalAccessException e) {
                logger.error("Cannot set the value of field {} on class {}", field.getName(), entityClass.getName());
            }
        }
    }

    private <T> void processMethods(final WebDriver driver, final SearchContext searchContext,
                                    final Class<T> entityClass, final T instance, final List<String> cssSelectors) {
        for (Method method : entityClass.getDeclaredMethods()) {
            CssSelector annotation = method.getAnnotation(CssSelector.class);
            if (annotation == null) {
                continue;
            }

            Object[] parameters = getParametersValues(driver, searchContext, cssSelectors, method, annotation);

            try {
                method.invoke(instance, parameters);
            } catch (IllegalAccessException e) {
                logger.error("Cannot call the method {} on class {}", method.getName(), entityClass.getName());
            } catch (InvocationTargetException e) {
                logger.error("Cannot call the method {} on class {}", method.getName(), entityClass.getName());
            }
        }
    }

    private Object[] getParametersValues(final WebDriver driver, final SearchContext searchContext,
                                         final List<String> cssSelectors, final Method method,
                                         final CssSelector annotation) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = valueMapper.getValue(driver, searchContext, annotation, parameterTypes[i],
                    genericParameterTypes[i], cssSelectors);
        }
        return parameters;
    }
}
