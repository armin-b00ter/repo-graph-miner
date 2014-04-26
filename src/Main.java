import graph_builder.GraphBuilder;
import graph_builder.GraphConverterUtil;

import java.util.Scanner;

import config.IConfigurer;


public class Main {

	public static void main(String[] args) {
		ConfigFactory cf = new ConfigFactory();
		System.out.println(args[2]);
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
				String graphName = args[1] + "_" + args[2];
				builder.setConfigurer(config);
				builder.makeGraph(graphName);
				GraphConverterUtil conv = new GraphConverterUtil(graphName);
				conv.convertGraph("converted_" + graphName);
				conv.saveConversion("mapping_" + graphName);
				System.out.println("Graph files has been created successfully.");
			}
		}
		else if(args.length>=2 && args[0].equals("mine"))
		{
			
		}
		else
		{
			System.out.println("Invalid action name");
			printUsageRules();
		}	
	}
	
	private static void printUsageRules()
	{
		ConfigFactory cf = new ConfigFactory();
		System.out.println("Usage Parameters: [action_name] [parameter1, parameter2, ...]");
		System.out.println("ACTION: \ngraph ( to generate graph )");
		System.out.println("\tgraph [" + cf.getBuildersList() + "] [" + cf.getConfigurersList() + "]");
		System.out.println("mine( to mine cccs from changesets and calculate F1 )");
		System.out.println("\tmine [output_dir]");
	}

}
