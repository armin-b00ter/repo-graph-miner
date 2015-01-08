import evaluator.ConcernReader;
import evaluator.F1Calculator;
import evaluator.FolderF1Calculator;
import external.RRWExecuter;
import graph_builder.GraphBuilder;
import graph_builder.GraphConverterUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import config.IConfigurer;
import graph_builder.newgraph.IslandsAlgorithmExecuter;


public class Main {

    public static void main(String[] args) throws IOException {
        ConfigFactory cf = new ConfigFactory();
        if (args.length >= 3 && args[0].equals("graph")) {
            GraphBuilder builder = cf.getBuilder(args[1]);
            IConfigurer config = cf.getConfigurer(args[2]);
            if (builder == null || config == null) {
                System.out.println("Invalid Parameters.");
                printUsageRules();
            } else {
                new File("graph").mkdir();
                String graphName = "graph/" + args[1] + "_" + args[2];
                System.out.println("Loading configuration ...");
                builder.setConfigurer(config);
                System.out.println("Making graph ...");
                builder.makeGraph(graphName);
                System.out.println("Converting graph ...");
                builder.convertGraph(graphName);
                System.out.println("Graph files has been created successfully.");
            }
        } else if (args.length >= 3 && args[0].equals("mine")) {
            String outDir = "out/" + args[1] + "/" + args[2];
            new File(outDir).mkdirs();
            String inputGraphName = "graph/" + args[1] + "_" + args[2] + "_converted";
            IslandsAlgorithmExecuter ex = new IslandsAlgorithmExecuter(outDir, inputGraphName);
            handleRunRRW(ex);
        } else if (args.length >= 3 && args[0].equals("evaluate")) {
            GraphConverterUtil conv = new GraphConverterUtil();
            String graphMappingName = "graph/" + args[1] + "_" + args[2] + "_mapping";
            conv.loadConversion(graphMappingName);
            ConcernReader cccr = new ConcernReader(cf.getConfigurer(args[2]), conv);
            handleEvaluator(cccr, args[1], args[2]);
        } else {
            System.out.println("Invalid action name");
            printUsageRules();
        }
    }

    private static void handleEvaluator(ConcernReader cccr, String method, String caseStudy) {
        boolean getMethod = method.equals("file") ? false : true;
        HashMap<String, Set<String>> cccs = cccr.getConcernList(getMethod);
        F1Calculator f1 = new F1Calculator(cccs);
        System.out.println(cccs.size());
        new FolderF1Calculator("out/" + method + "/" + caseStudy).calculate(f1);

    }

    private static void handleRunRRW(IslandsAlgorithmExecuter ex) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Current parameters is:");
            System.out.println(ex.getParamValues());
            System.out.println("-to change the param value type param=value");
            System.out.println("-to run algorithm type run");
            System.out.println("-to quit type exit");
            String cmd = sc.nextLine();
            cmd.trim();
            if (cmd.equals("exit"))
                break;
            else if (cmd.equals("run"))
                ex.execute();
            else {
                String[] cmdArr = cmd.split("=");
                ex.getParams().put(cmdArr[0].trim(), cmdArr[1].trim());
            }
        }
    }

    private static void printUsageRules() {
        ConfigFactory cf = new ConfigFactory();
        System.out.println("Usage Parameters: [action_name] [parameter1, parameter2, ...]");
        System.out.println("ACTION: \ngraph ( to generate graph )");
        System.out.println("\tgraph [" + cf.getBuildersList() + "] [" + cf.getConfigurersList() + "]");
        System.out.println("mine( to mine cccs from changesets )");
        System.out.println("\tmine [" + cf.getBuildersList() + "] [" + cf.getConfigurersList() + "]");
        System.out.println("evaulate( to calculate F1 )");
        System.out.println("\tevaluate [" + cf.getBuildersList() + "] [" + cf.getConfigurersList() + "]");
    }

}
