package fr.kissy.mapper.ghost.test;

import org.openqa.selenium.WebElement;

/**
 * @author Guillaume Le Biller (<i>guillaume.lebiller@gmail.com</i>)
 * @version $Id$
 */
public class Bean_With_Valid_WebElement_Constructor {
    private String value;

    public Bean_With_Valid_WebElement_Constructor(String value) {
        this.value = value;
    }

    public Bean_With_Valid_WebElement_Constructor(WebElement value) {
        this.value = value.toString();
    }


    public Bean_With_Valid_WebElement_Constructor() {
        this.value = Bean_With_Valid_WebElement_Constructor.class.getSimpleName();
    }

    public String getValue() {
        return value;
    }
}
