package org.incava.jagol;

import java.io.*;
import java.util.*;


/**
 * A group of options.
 */
public class OptionSet
{
    private List options = new ArrayList();

    private List rcFiles = new ArrayList();

    private String appName;
    
    private String description;

    private List envVars = new ArrayList();
    
    public OptionSet(String appName, String description)
    {
        this.appName = appName;
        this.description = description;
    }
    
    /**
     * Returns the application name.
     */
    public String getAppName()
    {
        return appName;
    }

    /**
     * Returns the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Adds an options to this set.
     */
    public void add(Option opt)
    {
        options.add(opt);
    }

    /**
     * Adds a run control file to be processed.
     */
    public void addRunControlFile(String name)
    {
        rcFiles.add(name);
    }

    /**
     * Adds the given environment variable to the list of environment variables
     * from which to read user-defined options.
     */
    public void addEnvironmentVariable(String envVar) 
    {
        envVars.add(envVar);
    }

    /**
     * Processes the run control files and command line arguments. Returns the
     * arguments that were not consumed by option processing.
     */
    public String[] process(String[] args)
    {
        processRunControlFiles();
        // processEnvironmentVariables();

        return processCommandLine(args);
    }

    /**
     * Processes the run control files, if any.
     */
    protected void processRunControlFiles()
    {
        Iterator it = rcFiles.iterator();
        while (it.hasNext()) {
            String rcFileName = (String)it.next();
            try {
                Properties props = new Properties();

                int tildePos = rcFileName.indexOf('~');
                if (tildePos != -1) {
                    rcFileName = rcFileName.substring(0, tildePos) + System.getProperty("user.home") + rcFileName.substring(tildePos + 1);
                }

                props.load(new FileInputStream(rcFileName));

                Iterator pit = props.keySet().iterator();
                while (pit.hasNext()) {
                    String key   = (String)pit.next();
                    String value = (String)props.get(key);
                    Iterator oit = options.iterator();
                    boolean processed = false;
                    while (!processed && oit.hasNext()) {
                        Option opt = (Option)oit.next();
                        if (opt.getLongName().equals(key)) {
                            processed = true;
                            try {
                                opt.setValue(value);
                            }
                            catch (OptionException oe) {
                                System.err.println("error: " + oe.getMessage());
                            }
                        }
                    }
                }
            }
            catch (FileNotFoundException fnfe) {
                // ioe.printStackTrace();
            }
            catch (IOException ioe) {
                // ioe.printStackTrace();
            }
        }
    }

    /**
     * Processes the command line arguments. Returns the arguments that were not
     * consumed by option processing.
     */
    protected String[] processCommandLine(String[] args)
    {
        List argList = new ArrayList();
        for (int i = 0; i < args.length; ++i) {
            argList.add(args[i]);
        }

        while (argList.size() > 0) {
            String arg = (String)argList.get(0);
            
            if (arg.equals("--")) {
                argList.remove(0);
                break;
            }
            else if (arg.charAt(0) == '-') {
                argList.remove(0);
                
                Iterator oit = options.iterator();
                boolean processed = false;
                while (!processed && oit.hasNext()) {
                    Option opt = (Option)oit.next();
                    try {
                        processed = opt.set(arg, argList);
                    }
                    catch (OptionException oe) {
                        System.err.println("error: " + oe.getMessage());
                    }
                }

                if (!processed) {
                    if (arg.equals("--help") || arg.equals("-h")) {
                        showUsage();
                    }
                    else if (rcFiles.size() > 0 && arg.equals("--help-config")) {
                        showConfig();
                    }
                    else {
                        System.err.println("invalid option: " + arg + " (-h will show valid options)");
                    }
                    
                    break;
                }
            }
            else {
                break;
            }
        }

        String[] unprocessed = (String[])argList.toArray(new String[0]);
        
        return unprocessed;
    }

    protected void showUsage()
    {
        System.out.println("Usage: " + appName + " [options] file...");
        System.out.println(description);
        System.out.println();
        System.out.println("Options:");

        List tags = new ArrayList();

        Iterator it = options.iterator();
        while (it.hasNext()) {
            Option opt = (Option)it.next();
            StringBuffer buf = new StringBuffer("  ");

            if (opt.getShortName() == 0) {
                buf.append("    ");
            }
            else {
                buf.append("-" + opt.getShortName() + ", ");
            }
                            
            buf.append("--" + opt.getLongName());
                            
            tags.add(buf.toString());
        }
                        
        int widest = -1;
        Iterator tit = tags.iterator();
        while (tit.hasNext()) {
            String tag = (String)tit.next();
            widest = Math.max(tag.length(), widest);
        }

        it = options.iterator();
        tit = tags.iterator();
        while (it.hasNext()) {
            Option opt = (Option)it.next();
            String tag = (String)tit.next();

            System.out.print(tag);
            for (int i = tag.length(); i < widest + 2; ++i) {
                System.out.print(" ");
            }

            System.out.println(opt.getDescription());
        }

        if (rcFiles.size() > 0) {
            System.out.println("For an example configure file, run --help-config");
            System.out.println();
            System.out.println("Configuration File" + (rcFiles.size() > 1 ? "s" : "") + ":");
            Iterator rit = rcFiles.iterator();
            while (rit.hasNext()) {
                String rcFileName = (String)rit.next();
                System.out.println("    " + rcFileName);
            }
        }
    }

    protected void showConfig()
    {
        Iterator it = options.iterator();
        while (it.hasNext()) {
            Option opt = (Option)it.next();
            System.out.println("# " + opt.getDescription());
            System.out.println(opt.getLongName() + " = " + opt.toString());
            System.out.println();
        }
    }
}
