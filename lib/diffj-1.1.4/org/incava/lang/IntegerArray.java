package org.incava.lang;

import java.io.*;
import java.util.*;


/**
 * An array that allows insertion at any point, not within the set size.
 */
public class IntegerArray
{
    private TreeMap data;

    public IntegerArray()
    {
        data = new TreeMap();
    }

    public Integer get(int index)
    {
        return get(new Integer(index));
    }

    public Integer get(Integer index)
    {
        return (Integer)data.get(index);
    }

    public void set(int index, int value)
    {
        set(new Integer(index), new Integer(value));
    }

    public void set(int index, Integer value)
    {
        set(new Integer(index), value);
    }

    public void set(Integer index, Integer value)
    {
        data.put(index, value);
    }

    public void remove(Integer index)
    {
        data.remove(index);
    }

    public int size()
    {
        return data.size() == 0 ? 0 : 1 + ((Integer)lastKey()).intValue();
    }

    public Integer lastKey()
    {
        return lastIndex();
    }

    public Integer lastIndex()
    {
        return data.size() == 0 ? null : (Integer)data.lastKey();
    }

    public Integer lastValue()
    {
        return get(lastKey());
    }

    public String toString()
    {
        return data.toString();
    }

    public void add(Integer value)
    {
        int last = size();
        set(last, value);
    }

    public void add(int value)
    {
        add(new Integer(value));
    }

    public Integer[] toArray()
    {
        Integer[] ary = new Integer[size()];
        Iterator it = data.keySet().iterator();
        while (it.hasNext()) {
            Integer idx = (Integer)it.next();
            Integer val = (Integer)data.get(idx);
            ary[idx.intValue()] = val;
        }
        return ary;
    }

}
