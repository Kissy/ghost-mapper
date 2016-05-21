package fr.kissy.mapper.ghost;

import com.google.common.collect.Lists;
import fr.kissy.mapper.ghost.annotation.CssSelector;
import fr.kissy.mapper.ghost.exception.NullConstructorException;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Guillaume Le Biller (<i>guillaume.lebiller@gmail.com</i>)
 * @version $Id$
 */
public class GhostBeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(GhostBeanFactory.class);

    private GhostValueMapper valueMapper;
    private GhostConstructorFactory constructorFactory;

    protected GhostBeanFactory(final GhostValueMapper valueMapper) {
        this.valueMapper = valueMapper;
        this.constructorFactory = new GhostConstructorFactory();
    }

    @SuppressWarnings("unchecked")
    public <T> T createInstance(final WebDriver driver, final SearchContext searchContext,
                                final Class<T> objectType, final List<String> rootCssSelectors) {
        final Constructor<?> constructor = constructorFactory.getConstructor(objectType);
        final CssSelector annotation = constructor.getAnnotation(CssSelector.class);
        final Class<?>[] parameterTypes = constructor.getParameterTypes();

        final List<Object> parameters = Lists.newArrayList();
        for (int i = 0; i < parameterTypes.length; i++) {
            final T value = (T) valueMapper.getValue(driver, searchContext, annotation, parameterTypes[i],
                    constructor.getGenericParameterTypes()[i], rootCssSelectors);
            parameters.add(value);
        }

        if (parameters.size() != parameterTypes.length) {
            logger.error("Impossible to find an accessible constructor for class {}", objectType.getName());
            return null;
        }

        try {
            return ConstructorUtils.invokeConstructor(objectType, parameters.toArray(), parameterTypes);
        } catch (InvocationTargetException e) {
            if (e.getTargetException().getClass().equals(NullConstructorException.class)) {
                return null;
            } else {
                logger.error("Impossible to find construct the instance of {} : {}", objectType.getName(), e);
                throw new IllegalStateException("Impossible to find construct the instance");
            }
        } catch (Exception e) {
            logger.error("Impossible to find construct the instance of {} : {}", objectType.getName(), e);
            throw new IllegalStateException("Impossible to find construct the instance");
        }
    }
}
