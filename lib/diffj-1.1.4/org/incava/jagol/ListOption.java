package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * Represents a list of objects that comprise this option.
 */
public class ListOption extends Option
{
    private List value;
    
    /**
     * Creates the option.
     */
    public ListOption(String longName, String description)
    {
        super(longName, description);
        
        value = new ArrayList();
    }

    /**
     * Returns the value. This is empty by default.
     */
    public List getValue()
    {
        return value;
    }

    /**
     * Sets the value.
     */
    public void setValue(List value)
    {
        this.value = value;
    }

    /**
     * Sets the value from the string, for a list type. Assumes whitespace or
     * comma delimiter
     */
    public void setValue(String value) throws InvalidTypeException
    {
        parse(value);
    }

    /**
     * Sets from a list of command-line arguments. Returns whether this option
     * could be set from the current head of the list. Assumes whitespace or
     * comma delimiter.
     */
    public boolean set(String arg, List args) throws OptionException
    {
        if (arg.equals("--" + longName)) {
            if (args.size() == 0) {
                throw new InvalidTypeException(longName + " expects following argument");
            }
            else {
                String value = (String)args.remove(0);
                setValue(value);
            }
        }
        else if (arg.startsWith("--" + longName + "=")) {
            int pos = ("--" + longName + "=").length();
            if (pos >= arg.length()) {
                throw new InvalidTypeException(longName + " expects argument");
            }
            else {
                String value = arg.substring(pos);
                setValue(value);
            }
        }
        else if (shortName != 0 && arg.equals("-" + shortName)) {
            if (args.size() == 0) {
                throw new InvalidTypeException(shortName + " expects following argument");
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
     * Parses the value into the value list. If subclasses want to convert the
     * string to their own data type, override the <code>convert</code> method.
     *
     * @see ListOption#convert(String)
     */
    protected void parse(String str) throws InvalidTypeException
    {
        List     list = listify(str);
        Iterator it   = list.iterator();
        while (it.hasNext()) {
            String s = (String)it.next();
            if (!s.equals("+=")) {
                value.add(convert(s));
            }
        }
    }

    /**
     * Converts the (possibly quoted) string into a list, delimited by
     * whitespace and commas..
     */
    public static List listify(String str)
    {
        // strip leading/trailing single/double quotes
        if (str.charAt(0) == str.charAt(str.length() - 1) &&
            (str.charAt(0) == '"' || str.charAt(0) == '\'')) {
            str = str.substring(1, str.length() - 1);
        }
        
        List list = new ArrayList();
        StringTokenizer st = new StringTokenizer(str, " \t\n\r\f,");
        while (st.hasMoreTokens()) {
            String tk = st.nextToken();
            list.add(tk);
        }
        return list;
    }

    /**
     * Returns the string, possibly converted to a different Object type. 
     * Subclasses can convert the string to their own data type.
     */
    protected Object convert(String str) throws InvalidTypeException
    {
        return str;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        Iterator it = value.iterator();
        boolean isFirst = true;
        while (it.hasNext()) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                buf.append(", ");
            }
            buf.append(it.next());
        }
        return buf.toString();
    }

}
