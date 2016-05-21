package fr.kissy.mapper.ghost.test;

import fr.kissy.mapper.ghost.annotation.CssSelector;
import org.openqa.selenium.WebElement;

/**
 * @author Guillaume Le Biller (<i>guillaume.lebiller@gmail.com</i>)
 * @version $Id$
 */
public class Bean_With_Valid_Annotated_Constructor {
    private String value;

    public Bean_With_Valid_Annotated_Constructor(String value) {
        this.value = value;
    }

    public Bean_With_Valid_Annotated_Constructor(WebElement value) {
        this.value = value.toString();
    }

    @CssSelector
    public Bean_With_Valid_Annotated_Constructor(String value, WebElement element) {
        this.value = value;
    }


    public Bean_With_Valid_Annotated_Constructor() {
        this.value = Bean_With_Valid_Annotated_Constructor.class.getSimpleName();
    }

    public String getValue() {
        return value;
    }
}
