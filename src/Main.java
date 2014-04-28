import external.RRWExecuter;
import graph_builder.GraphBuilder;
import graph_builder.GraphConverterUtil;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import config.IConfigurer;


public class Main {

	public static void main(String[] args) throws IOException {
		ConfigFactory cf = new ConfigFactory();
		if(args.length>=3 && args[0].equals("graph"))
		{
			GraphBuilder builder = cf.getBuilder(args[1]);
			IConfigurer config = cf.getConfigurer(args[2]);
			if(builder==null || config==null)
			{
				System.out.println("Invalid Parameters.");
				printUsageRules();
			}
			else
			{
				String graphName = "graph\\" + args[1] + "_" + args[2];
				System.out.println("Loading configuration ...");
				builder.setConfigurer(config);
				System.out.println("Making graph ...");
				builder.makeGraph(graphName);
				System.out.println("Converting graph ...");
				GraphConverterUtil conv = new GraphConverterUtil(graphName);
				conv.convertGraph(graphName + "_converted");
				conv.saveConversion(graphName + "_mapping");
				System.out.println("Graph files has been created successfully.");
			}
		}
		else if(args.length>=3 && args[0].equals("mine"))
		{
			String outDir = "out\\" + args[1] + "\\" + args[2];
			new File(outDir).mkdirs();
			String inputGraphName = "graph\\" + args[1] + "_" + args[2] + "_converted";
			RRWExecuter ex = new RRWExecuter(outDir, inputGraphName);
			handleRunRRW(ex);
		}
		else
		{
			System.out.println("Invalid action name");
			printUsageRules();
		}	
	}
	
	private static void handleRunRRW(RRWExecuter ex) throws IOException
	{
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.println("Current parameters is:");
			System.out.println(ex.getParamValues());
			System.out.println("-to change the param value type param=value");
			System.out.println("-to run algorithm type run");
			System.out.println("-to quit type exit");
			String cmd = sc.nextLine();
			cmd.trim();
			if(cmd.equals("exit"))
				break;
			else if(cmd.equals("run"))
				ex.execute();
			else
			{
				String[] cmdArr = cmd.split("=");
				ex.params.put(cmdArr[0].trim(), cmdArr[1].trim()); 
			}
		}
	}
	
	private static void printUsageRules()
	{
		ConfigFactory cf = new ConfigFactory();
		System.out.println("Usage Parameters: [action_name] [parameter1, parameter2, ...]");
		System.out.println("ACTION: \ngraph ( to generate graph )");
		System.out.println("\tgraph [" + cf.getBuildersList() + "] [" + cf.getConfigurersList() + "]");
		System.out.println("mine( to mine cccs from changesets and calculate F1 )");
		System.out.println("\tmine [" + cf.getBuildersList() + "] [" + cf.getConfigurersList() + "]");
	}

}
