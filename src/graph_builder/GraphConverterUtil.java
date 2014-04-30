package graph_builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class GraphConverterUtil {
	private String inputFile = null;
	private HashMap<String, Integer> nodeDict = null;
	private int nodeIDCounter = 1;
	
	public GraphConverterUtil()
	{
		nodeDict = new HashMap<String, Integer>();
	}
	
	public GraphConverterUtil(String inputFile)
	{
		this.inputFile = inputFile;
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
		Scanner sc = null;
		BufferedWriter  bw = null;
		try
		{
			sc = new Scanner(new File(inputFile));
			bw = new BufferedWriter(new FileWriter(new File(outputFile)));
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				String weightedEdge[] = line.split("\t");
				bw.write(mapNode(weightedEdge[0]) + "\t" + mapNode(weightedEdge[1]) + "\t" + weightedEdge[2] + "\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			sc.close();
			try {
				bw.close();
			} catch (IOException e) {
			}
		}
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
		HashMap<String, Integer> nodeMaps = new HashMap<String, Integer>();
		try
		{
			sc = new Scanner(new File(inputFile));
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				String mapNode[] = line.split("\t");
				nodeMaps.put(mapNode[0], Integer.parseInt(mapNode[1]));
			}
			nodeDict = nodeMaps;
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
