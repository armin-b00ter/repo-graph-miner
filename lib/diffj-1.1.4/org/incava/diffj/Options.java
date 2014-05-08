package org.incava.diffj;

import java.io.*;
import java.util.*;
import org.incava.analysis.*;
import org.incava.jagol.*;
import org.incava.lang.StringExt;


/**
 * Options.
 */
public class Options extends OptionSet
{
    public static int MAXIMUM_WARNING_LEVEL = 100;

    public static int MINIMUM_WARNING_LEVEL = -1;

    public static int DEFAULT_WARNING_LEVEL = -1;

    /**
     * Whether to use brief or detailed reporting.
     */
    public static boolean briefOutput = false;

    /**
     * The version.
     */
    public static String VERSION = "1.1.4";

    public static String DEFAULT_SOURCE = "1.5";

    /**
     * The Java source version, of the from-file.
     */
    public static String fromSource = DEFAULT_SOURCE;

    /**
     * The Java source version, of the to-file.
     */
    public static String toSource = DEFAULT_SOURCE;

    /**
     * Whether to recurse.
     */
    public static Boolean recurse = Boolean.FALSE;

    /**
     * The brief option.
     */
    private BooleanOption briefOpt;

    /**
     * The tab width option.
     */
    private IntegerOption tabWidthOpt;

    /**
     * The verbose option.
     */
    private BooleanOption verboseOpt;

    /**
     * The version option.
     */
    private BooleanOption versionOpt;

    /**
     * The from-source option.
     */
    private StringOption fromSourceOpt;

    /**
     * The to-source option.
     */
    private StringOption toSourceOpt;

    /**
     * The source option.
     */
    private StringOption sourceOpt;

    /**
     * Whether to process directories recursively.
     */
    private BooleanOption recurseOpt;

    private static Options instance = null;

    public static Options get()
    {
        if (instance == null) {
            instance = new Options();
        }
        return instance;
    }

    protected Options()
    {
        super("diffj", "Analyzes and validates Java and Javadoc");

        // use the diffj.* equivalent property for each option.

        Boolean brief = new Boolean(briefOutput);
        String briefProperty = System.getProperty("diffj.brief");
        tr.Ace.log("brief property: " + briefProperty);
        if (briefProperty != null) {
            brief = new Boolean(briefProperty);
            tr.Ace.log("brief: " + brief);

            briefOutput = brief.booleanValue();
        }

        Integer tabWidth = new Integer(DetailedReport.tabWidth);
        String tabWidthProperty = System.getProperty("diffj.tabwidth");
        if (tabWidthProperty != null) {
            tabWidth = new Integer(tabWidthProperty);
            DetailedReport.tabWidth = tabWidth.intValue();
        }

        Boolean verbose = new Boolean(false);
        String verboseProperty = System.getProperty("diffj.verbose");
        if (verboseProperty != null) {
            verbose = new Boolean(verboseProperty);
        }

        add(briefOpt      = new BooleanOption("brief",     "Display output in brief form"));
        add(tabWidthOpt   = new IntegerOption("tabwidth",  "the number of spaces to treat tabs equal to"));
        add(recurseOpt    = new BooleanOption("recurse",   "process directories recursively"));
        add(verboseOpt    = new BooleanOption("verbose",   "whether to run in verbose mode (for debugging)"));
        add(versionOpt    = new BooleanOption("version",   "Displays the version"));
        recurseOpt.setShortName('r');
        versionOpt.setShortName('v');

        add(fromSourceOpt = new StringOption("from-source", "the Java source version, of the from-file; 1.3, 1.4, 1.5 (the default), or 1.6"));
        add(toSourceOpt   = new StringOption("to-source",   "the Java source version, of the to-file; 1.3, 1.4, 1.5 (the default), or 1.6"));
        add(sourceOpt     = new StringOption("source",      "the Java source version of both from-file and the to-file"));
        
        // addEnvironmentVariable("DIFFJ_PROPERTIES");
        
        addRunControlFile("/etc/diffj.conf");
        addRunControlFile("~/.diffjrc");
    }

    /**
     * Processes the run control files and command line arguments, and sets the
     * static variables. Returns the arguments that were not consumed by option
     * processing.
     */
    public String[] process(String[] args)
    {
        tr.Ace.log("args: " + args);
        String[] unprocessed = super.process(args);

        Integer tabWidthInt = tabWidthOpt.getValue();
        if (tabWidthInt != null) {
            tr.Ace.log("setting tab width: " + tabWidthInt);
            DetailedReport.tabWidth = tabWidthInt.intValue();
        }
    
        Boolean briefBool = briefOpt.getValue();
        if (briefBool != null) {
            tr.Ace.log("setting brief: " + briefBool);
            briefOutput = briefBool.booleanValue();
        }
        
        Boolean recurseBool = recurseOpt.getValue();
        if (recurseBool != null) {
            tr.Ace.log("setting recurse: " + recurseBool);
            recurse = recurseBool;
        }

        Boolean verboseBool = verboseOpt.getValue();
        if (verboseBool != null) {
            tr.Ace.log("setting verbose: " + verboseBool);
            tr.Ace.setVerbose(verboseBool.booleanValue());
        }

        Boolean versionBool = versionOpt.getValue();
        if (versionBool != null) {
            System.out.println("diffj, version " + VERSION);
            System.out.println("Written by Jeff Pace (jpace [at] incava [dot] org)");
            System.out.println("Released under the Lesser GNU Public License");
            System.exit(0);
        }

        String sourceStr = sourceOpt.getValue();
        if (sourceStr != null) {
            tr.Ace.log("setting fromSource: " + sourceStr);
            fromSource = sourceStr;
            tr.Ace.log("setting toSource: " + sourceStr);
            toSource = sourceStr;
        }

        String fromSourceStr = fromSourceOpt.getValue();
        if (fromSourceStr != null) {
            tr.Ace.log("setting fromSource: " + fromSourceStr);
            fromSource = fromSourceStr;
        }

        String toSourceStr = toSourceOpt.getValue();
        if (toSourceStr != null) {
            tr.Ace.log("setting toSource: " + toSourceStr);
            toSource = toSourceStr;
        }

        tr.Ace.log("unprocessed", unprocessed);
        
        return unprocessed;
    }
}
