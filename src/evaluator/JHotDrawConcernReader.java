package evaluator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JHotDrawConcernReader implements IConcernReader {
	String inputPath;
	
	public JHotDrawConcernReader(String inputPath)
	{
		this.inputPath = inputPath;
	}
	
	@Override
	public ArrayList<Set<String>> getConcernList(boolean getMethod) 
	{
		ArrayList<Set<String>> ret = new  ArrayList<Set<String>>();
		ret.addAll(doubleHashReader(inputPath + "cross_jhd_dynamo.txt", getMethod));
		ret.addAll(doubleHashReader(inputPath + "cross_jhd_ident.txt", getMethod));
		ret.addAll(jhdFaninReader(getMethod));
		if(!getMethod)
			ret.addAll(cccFiles());
		return ret;
	}
	
	private ArrayList<Set<String>> doubleHashReader(String file, boolean getMethod)
	{
		try{
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			ArrayList<Set<String>> ret = new ArrayList<Set<String>>();
			
			while ((strLine = br.readLine()) != null)
			{
				Set<String> temp = new HashSet<String>();
				String files = strLine.substring(strLine.indexOf("##")+2);
				for(String fileName: files.split(";"))
				{
					if(getMethod)
						temp.add(fileName.substring(0, fileName.lastIndexOf('.')).replace('.', '/') + ".java");
					else
						temp.add(fileName.substring(0, fileName.lastIndexOf('.')).replace('.', '/') + ".java");
				}
				ret.add(temp);
			}
			in.close();
			
			return ret;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
		
	private ArrayList<Set<String>> jhdFaninReader(boolean getMethod)
	{
		try
		{
			DataInputStream in = new DataInputStream(new FileInputStream(inputPath + "cross_jhd_fanin.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			ArrayList<Set<String>> ret = new ArrayList<Set<String>>();
			
			while ((strLine = br.readLine()) != null)
			{
				Set<String> temp = new HashSet<String>();
				String files = strLine.substring(strLine.indexOf('#')+1).replace('#', ';');
				for(String fileName: files.split(";"))
				{
					if(getMethod)
						temp.add(fileName.substring(0, fileName.lastIndexOf('.')).replace('.', '/') + ".java");
					else
						temp.add(fileName.substring(0, fileName.lastIndexOf('.')).replace('.', '/') + ".java");
				}
				ret.add(temp);
			}
		    in.close();
		    
		    return ret;
		}
	    catch(IOException e)
		{
	    	e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<Set<String>> cccFiles()
	{
		try
		{
			DataInputStream in = new DataInputStream(new FileInputStream(inputPath + "crosscuts_files.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			ArrayList<Set<String>> ret = new ArrayList<Set<String>>();
			
			while ((strLine = br.readLine()) != null)
			{
				if(strLine.isEmpty())
					continue;
				Set<String> temp = new HashSet<String>();
				String files = strLine.substring(strLine.indexOf("=[")+2, strLine.length()-3);
				for(String fileName: files.split(","))
				{
					temp.add(fileName.trim());
				}
				ret.add(temp);
			}
		    in.close();
		    
		    return ret;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
