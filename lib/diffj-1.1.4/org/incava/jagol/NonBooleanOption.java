package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Base class of all options, except for booleans.
 */
public abstract class NonBooleanOption extends Option
{
    public NonBooleanOption(String longName, String description)
    {
        super(longName, description);
    }

    /**
     * Sets from a list of command-line arguments. Returns whether this option
     * could be set from the current head of the list.
     */
    public boolean set(String arg, List args) throws OptionException
    {
        if (arg.equals("--" + longName)) {
            if (args.size() == 0) {
                throw new InvalidTypeException(longName + " expects following " + getType() + " argument");
            }
            else {
                String value = (String)args.remove(0);
                setValue(value);
            }
        }
        else if (arg.startsWith("--" + longName + "=")) {
            int pos = ("--" + longName + "=").length();
            if (pos >= arg.length()) {
                throw new InvalidTypeException(longName + " expects argument of type " + getType());
            }
            else {
                String value = arg.substring(pos);
                setValue(value);
            }
        }
        else if (shortName != 0 && arg.equals("-" + shortName)) {
            if (args.size() == 0) {
                throw new InvalidTypeException(shortName + " expects following " + getType() + " argument");
            }
            else {
                String value = (String)args.remove(0);
                setValue(value);
            }                
        }
        else {
            return false;
        }
        return true;
    }

    /**
     * Returns the option type.
     */
    protected abstract String getType();

}
