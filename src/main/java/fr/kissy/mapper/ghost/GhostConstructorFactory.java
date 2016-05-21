package fr.kissy.mapper.ghost;

import fr.kissy.mapper.ghost.annotation.CssSelector;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * @author Guillaume Le Biller (<i>guillaume.lebiller@gmail.com</i>)
 * @version $Id$
 */
public class GhostConstructorFactory {
    private static final Logger logger = LoggerFactory.getLogger(GhostConstructorFactory.class);

    protected GhostConstructorFactory() {
    }

    public <T> Constructor<?> getConstructor(final Class<T> objectType) {
        Constructor<?> foundConstructor = null;
        // Find annotated constructor
        for (Constructor<?> constructor : objectType.getConstructors()) {
            final CssSelector annotation = constructor.getAnnotation(CssSelector.class);
            if (annotation == null) {
                continue;
            }
            foundConstructor = constructor;
            break;
        }
        // Fallback as WebElement constructor
        if (foundConstructor == null) {
            foundConstructor = ConstructorUtils.getAccessibleConstructor(objectType, WebElement.class);
        }
        // Fallback as Default constructor
        if (foundConstructor == null) {
            foundConstructor = ConstructorUtils.getAccessibleConstructor(objectType);
        }
        if (foundConstructor == null) {
            logger.error("Impossible to find an accessible constructor for class {}", objectType.getName());
            throw new IllegalStateException("Impossible to find an accessible constructor");
        }
        return foundConstructor;
    }
}
