package org.incava.analysis;

import java.awt.Point;
import java.io.*;
import java.util.*;
import org.incava.lang.StringExt;
import org.incava.qualog.Qualog;
import org.incava.util.ANSI;


/**
 * Reports differences in long form.
 */
public class DetailedReport extends Report
{
    /**
     * The number of spaces a tab is equivalent to.
     */
    public static int tabWidth = 4;

    /**
     * The reader associated with the from-file, which is used for reproducing
     * the code associated with a reference.
     */
    private Reader fromFileRdr;

    /**
     * The reader associated with the to-file, which is used for reproducing the
     * code associated with a reference.
     */
    private Reader toFileRdr;

    /**
     * The from-contents, separated by new lines, which are included at the end
     * of each string.
     */
    private String[] fromContents;

    /**
     * The to-contents, separated by new lines, which are included at the end
     * of each string.
     */
    private String[] toContents;

    public DetailedReport(Writer writer)
    {
        super(writer);
    }

    public DetailedReport(OutputStream os)
    {
        super(os);
    }

    public DetailedReport(Writer writer, String fromSource, String toSource)
    {
        super(writer, fromSource, toSource);
    }

    public DetailedReport(Writer writer, File fromFile, File toFile)
    {
        super(writer, fromFile, toFile);
    }

    public DetailedReport(OutputStream os, String fromSource, String toSource)
    {
        super(os, fromSource, toSource);
    }

    public DetailedReport(OutputStream os, File fromFile, File toFile)
    {
        super(os, fromFile, toFile);
    }
    
    /**
     * Associates the given file with the list of references, including that are
     * adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(File fromFile, File toFile)
    {
        super.reset(fromFile, toFile);

        tr.Ace.log("fromFile: " + fromFile + "; toFile: " + toFile);
        
        fromContents = null;
        toContents = null;
        
        try {
            fromFileRdr = new FileReader(fromFile);
            toFileRdr   = new FileReader(toFile);
        }
        catch (IOException ioe) {
            tr.Ace.log("error reading files: " + fromFile + ", " + toFile);
        }
    }

    /**
     * Associates the given string source with the list of references, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(String fromSource, String toSource)
    {
        super.reset(fromSource, toSource);

        fromContents = null;
        toContents = null;
        
        fromFileRdr = new StringReader(fromSource);
        toFileRdr = new StringReader(toSource);
    }

    /**
     * Associates the given string source with the list of differences, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(String fromFileName, String fromContents, String toFileName, String toContents)
    {
        super.reset(fromFileName, fromContents, toFileName, toContents);

        this.fromContents = null;
        this.toContents = null;
        
        fromFileRdr = new StringReader(fromContents);
        toFileRdr = new StringReader(toContents);
    }

    /**
     * Writes all differences, and clears the list.
     */
    public void flush()
    {
        if (differences.size() > 0) {
            printFileNames();
            try {
                tr.Ace.log("flushing differences");
                Collection diffs = collateDifferences(differences);
                Iterator it = diffs.iterator();
                while (it.hasNext()) {
                    Object        obj = it.next();
                    CodeReference ref = (CodeReference)obj;
                    String        str = toString(ref);
                    // tr.Ace.log("ref    : " + ref);
                    // tr.Ace.log("writing: " + str);
                    writer.write(str);
                }
                // we can't close STDOUT
                writer.flush();
                // writer.close();
            }
            catch (IOException ioe) {
            }
        }
        clear();
    }

    protected String[] getContents(Reader rdr)
    {
        try {
            List           cont = new ArrayList();
            BufferedReader br   = new BufferedReader(rdr);
            
            String line = br.readLine();
            while (line != null) {
                cont.add(line);
                line = br.readLine();
            }
            
            String[] contents = (String[])cont.toArray(new String[0]);            
            return contents;
        }
        catch (IOException ioe) {
            tr.Ace.log("error reading source: " + ioe);
            return null;
        }
    }

    /**
     * Returns a string representing the given reference, consistent with the
     * format of the Report subclass.
     */
    protected String toString(CodeReference ref)
    {
        StringBuffer buf = new StringBuffer();

        if (fromContents == null) {
            fromContents = getContents(fromFileRdr);
        }

        if (toContents == null) {
            toContents = getContents(toFileRdr);
        }
        
        // tr.Ace.cyan("writing: " + ref);

        Point  del = new Point(ref.firstStart.x,  ref.firstEnd.x);
        Point  add = new Point(ref.secondStart.x, ref.secondEnd.x);
        String ind = ref.type;

        buf.append(toString(del));
        buf.append(ind);
        buf.append(toString(add));
        buf.append(' ');
        buf.append(ref.message);
        buf.append(EOLN);

        if (ind.equals(CodeReference.ADDED)) {
            del = null;
        }
        else if (ind.equals(CodeReference.DELETED)) {
            add = null;
        }
        else if (ind.equals(CodeReference.CHANGED)) {
        }            
        
        if (del != null) {
            printLines(buf, del, "<", fromContents);
            if (add != null) {
                buf.append("---");
                buf.append(EOLN);
            }
        }
        if (add != null) {
            printLines(buf, add, ">", toContents);
        }

        buf.append(EOLN);

        // tr.Ace.cyan("str: " + buf);
        
        return buf.toString();
    }

    protected void printLines(StringBuffer buf, Point pt, String ind, String[] lines)
    {
        // tr.Ace.log("pt: (" + pt.x + ", " + pt.y + "); ind: " + ind + "; #lines: " + lines.length);
        for (int lnum = pt.x; lnum <= pt.y; ++lnum) {
            buf.append(ind + " " + lines[lnum - 1]);
            buf.append(EOLN);
        }
    }

}
