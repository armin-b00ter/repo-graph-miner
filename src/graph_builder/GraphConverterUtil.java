package graph_builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

public class GraphConverterUtil {
	private String inputFile = null;
	private HashMap<String, Integer> nodeDict = null;
	private int nodeIDCounter = 1;
	Graph graph;
	
	public GraphConverterUtil()
	{
		nodeDict = new HashMap<String, Integer>();
	}
	
	public GraphConverterUtil(Graph graph)
	{
		this.graph = graph;
		nodeDict = new HashMap<String, Integer>();
	}
	
	public int mapNode(String nodeID)
	{
		if(nodeDict==null)
			return -1;
		nodeID = nodeID.trim();
		if(nodeDict.containsKey(nodeID))
			return nodeDict.get(nodeID);
		nodeDict.put(nodeID, nodeIDCounter);
		nodeIDCounter++;
		return nodeIDCounter - 1;
	}
	
	public void convertGraph(String outputFile)
	{
		Graph myGraph = new Graph();
		for(String key_x: graph.edges.keySet())
			for(String key_y: graph.edges.get(key_x).keySet())
				myGraph.incrementEdge(String.valueOf(mapNode(key_x)), String.valueOf(mapNode(key_y)), graph.edges.get(key_x).get(key_y));

		myGraph.save(outputFile, true);
	}
	
	public void saveConversion(String outputFile)
	{
		BufferedWriter  bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(new File(outputFile)));
			for(String key : nodeDict.keySet())
				bw.write(key + "\t" + nodeDict.get(key) + "\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				bw.close();
			} catch (IOException e) {
			}
		}
	}
	
	public void loadConversion(String inputFile)
	{
		Scanner sc = null;
		int counter = 0;
		HashMap<String, Integer> nodeMaps = new HashMap<String, Integer>();
		try
		{
			sc = new Scanner(new File(inputFile));
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				String mapNode[] = line.split("\t");
				nodeMaps.put(mapNode[0], Integer.parseInt(mapNode[1]));
				if(Integer.parseInt(mapNode[1])>counter)
					counter = Integer.parseInt(mapNode[1]);
			}
			nodeDict = nodeMaps;
			nodeIDCounter = counter+1;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			sc.close();
		}
	}
}

class MyIntegerComparator implements Comparator<String>
{
	@Override
	public int compare(String o1, String o2) {
		int i1 = Integer.parseInt(o1);
		int i2 = Integer.parseInt(o2);
		if(i1>i2)
			return 1;
		else if(i1<i2)
			return -1;
		else
			return 0;
	}
	
}
