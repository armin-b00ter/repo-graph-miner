package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Represents an option that is an float.
 */
public class FloatOption extends NonBooleanOption
{
    private Float value;
    
    public FloatOption(String longName, String description)
    {
        super(longName, description);
        
        value = null;
    }

    /**
     * Returns the value. This is null if not set.
     */
    public Float getValue()
    {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(Float value)
    {
        this.value = value;
    }

    /**
     * Sets the value from the string, for a float type.
     */
    public void setValue(String value) throws InvalidTypeException
    {
        try {
            setValue(new Float(value));
        }
        catch (NumberFormatException nfe) {
            throw new InvalidTypeException(longName + " expects float argument, not '" + value + "'");
        }
    }

    public String toString()
    {
        return value == null ? "" : value.toString();
    }

    protected String getType()
    {
        return "float";
    }

}
