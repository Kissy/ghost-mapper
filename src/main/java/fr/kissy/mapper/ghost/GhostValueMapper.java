package fr.kissy.mapper.ghost;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.kissy.mapper.ghost.annotation.CssSelector;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Guillaume Le Biller (<i>guillaume.lebiller@gmail.com</i>)
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class GhostValueMapper {
    private static final Logger logger = LoggerFactory.getLogger(GhostValueMapper.class);

    private GhostBeanMapper beanMapper;

    protected GhostValueMapper(final GhostBeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }

    public <T> T getValue(final WebDriver driver, final SearchContext searchContext, final CssSelector annotation,
                          final Class<T> valueType, final Type genericType, final List<String> cssSelectors) {
        List<String> childCssSelector = Lists.newArrayList(cssSelectors);
        if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
            childCssSelector.add(annotation.value());
        }

        T result = null;
        if (ClassUtils.isAssignable(valueType, Collection.class)) {
            if (genericType instanceof ParameterizedType) {
                Type parameterizedType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                result = getCollectionValue(driver, searchContext, valueType, parameterizedType, childCssSelector);
            }
        } else if (valueType.equals(String.class) || ClassUtils.isPrimitiveOrWrapper(valueType)) {
            final String value = getStringValue(driver, searchContext, annotation, childCssSelector);
            result = (T) ConvertUtils.convert(value, valueType);
        } else if (valueType.equals(WebElement.class)) {
            result = (T) findElement(searchContext, annotation, childCssSelector);
        } else if (valueType.equals(WebDriver.class)) {
            result = (T) driver;
        } else {
            result = beanMapper.readBean(driver, searchContext, valueType, childCssSelector);
        }
        return result;
    }

    private Collection<Object> createCollection(Type collectionType) {
        if (collectionType.equals(List.class)) {
            return Lists.newArrayList();
        } else if (collectionType.equals(Set.class)) {
            return Sets.newHashSet();
        }
        try {
            return (Collection<Object>) collectionType.getClass().newInstance();
        } catch (Exception ignored) {}

        logger.error("Impossible to instanciate value for type {}", collectionType.getClass());
        return null;
    }

    private <T> T getCollectionValue(WebDriver driver, SearchContext searchContext, Type collectionType,
                                    Type parameterizedType, List<String> cssSelector) {
        Class<?> elementClass = (Class<?>) parameterizedType;
        By byCssSelector = By.cssSelector(StringUtils.join(cssSelector, " "));
        if (ClassUtils.isAssignable(elementClass, WebElement.class)) {
            return (T) searchContext.findElements(byCssSelector);
        } else {
            Collection<Object> elements = createCollection(collectionType);
            for (WebElement element : searchContext.findElements(byCssSelector)) {
                List<String> emptySelector = Lists.newArrayList();
                CssSelector elementAnnotation = elementClass.getAnnotation(CssSelector.class);
                elements.add(getValue(driver, element, elementAnnotation, elementClass, null, emptySelector));
            }
            return (T) elements;
        }
    }

    private String getStringValue(WebDriver driver, SearchContext searchContext, CssSelector annotation,
                                  List<String> cssSelector) {
        final WebElement webElement = findElement(searchContext, annotation, cssSelector);
        try {
            // Element is null, return null
            if (webElement == null) {
                return null;
            }
            // Fallback to text when annotation is null
            if (annotation == null) {
                return webElement.getText();
            }
            // Select attribute
            if (StringUtils.isNotBlank(annotation.attribute())) {
                return webElement.getAttribute(annotation.attribute());
            }
            // Javascript executor if element is hidden
            if (ClassUtils.isAssignable(driver.getClass(), JavascriptExecutor.class)) {
                if (!webElement.isDisplayed()) {
                    return ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", webElement)
                            .toString();
                }
            }
            // Fallback to selecting text
            return webElement.getText();
        } catch (Exception e) {
            String annotationValue = annotation != null ? annotation.value() : null;
            logger.error("Impossible to get value for element {} with selector {}", webElement, annotationValue);
            return null;
        }
    }

    private WebElement findElement(SearchContext searchContext, CssSelector annotation,
                                   List<String> elementCssSelector) {
        if (elementCssSelector.isEmpty()) {
            return (WebElement) searchContext;
        }

        final By byCssSelector = By.cssSelector(StringUtils.join(elementCssSelector, " "));
        if (annotation.required()) {
            return searchContext.findElement(byCssSelector);
        }
        List<WebElement> foundElements = searchContext.findElements(byCssSelector);
        if (foundElements.isEmpty()) {
            return null;
        }
        return foundElements.get(0);
    }
}
