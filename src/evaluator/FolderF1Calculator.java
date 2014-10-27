package evaluator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FolderF1Calculator {
	String inputFolder;
	
	
	public FolderF1Calculator(String inputFolder)
	{
		this.inputFolder = inputFolder;
	}
	
	public void calculate(F1Calculator f1)
	{
		String files;
		File folder = new File(inputFolder);
		File[] listOfFiles = folder.listFiles(); 
		 
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if (listOfFiles[i].isFile()) 
			{
				files = listOfFiles[i].getName();
				ArrayList<Set<String>> concerns = getConcernsFromFile(listOfFiles[i]);
				float sum = 0;
				for(Set<String> list: concerns)
					sum += f1.calculateF1(list).maxF1;
				System.out.println(files + " : " + sum/concerns.size());
				
			}
		}
	}
	
	private ArrayList<Set<String>> getConcernsFromFile(File file)
	{
		try
		{
			ArrayList<Set<String>> ret = new ArrayList<Set<String>>();
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) 
			{
				String line = sc.nextLine().trim();
				String[] nodes = line.split("[\t| ]");
				Set<String> concern = new HashSet<String>(Arrays.asList(nodes));
				ret.add(concern);
			}
			return ret;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
}