package fr.kissy.mapper.ghost;

import fr.kissy.mapper.ghost.test.Bean_With_No_Valid_Constructor;
import fr.kissy.mapper.ghost.test.Bean_With_Valid_Annotated_Constructor;
import fr.kissy.mapper.ghost.test.Bean_With_Valid_Default_Constructor;
import fr.kissy.mapper.ghost.test.Bean_With_Valid_WebElement_Constructor;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;

public class GhostConstructorFactory_Should {

    @InjectMocks
    private GhostConstructorFactory constructorFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void use_annotated_constructor_if_present() throws Exception {
        Constructor<?> constructor = constructorFactory.getConstructor(Bean_With_Valid_Annotated_Constructor.class);
        assertEquals(ConstructorUtils.getAccessibleConstructor(Bean_With_Valid_Annotated_Constructor.class, String.class, WebElement.class), constructor);
    }

    @Test
    public void use_web_element_constructor_if_present() throws Exception {
        Constructor<?> constructor = constructorFactory.getConstructor(Bean_With_Valid_WebElement_Constructor.class);
        assertEquals(ConstructorUtils.getAccessibleConstructor(Bean_With_Valid_WebElement_Constructor.class, WebElement.class), constructor);
    }

    @Test
    public void use_default_constructor_if_present() throws Exception {
        Constructor<?> constructor = constructorFactory.getConstructor(Bean_With_Valid_Default_Constructor.class);
        assertEquals(ConstructorUtils.getAccessibleConstructor(Bean_With_Valid_Default_Constructor.class), constructor);
    }

    @Test(expected = IllegalStateException.class)
    public void throw_exception_if_no_constructor_founds() throws Exception {
        constructorFactory.getConstructor(Bean_With_No_Valid_Constructor.class);
    }
}