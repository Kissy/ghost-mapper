package fr.kissy.mapper.ghost.test;

/**
 * @author Guillaume Le Biller (<i>guillaume.lebiller@gmail.com</i>)
 * @version $Id$
 */
public class Bean_With_Valid_Default_Constructor {
    private String value;

    public Bean_With_Valid_Default_Constructor(String value) {
        this.value = value;
    }

    public Bean_With_Valid_Default_Constructor() {
        this.value = Bean_With_Valid_Default_Constructor.class.getSimpleName();
    }

    public String getValue() {
        return value;
    }
}
