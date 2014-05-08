package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Represents an option that is an String.
 */
public class StringOption extends NonBooleanOption
{
    private String value;
    
    public StringOption(String longName, String description)
    {
        super(longName, description);

        value = null;
    }

    /**
     * Returns the value.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    public String toString()
    {
        return value;
    }

    protected String getType()
    {
        return "string";
    }

}
