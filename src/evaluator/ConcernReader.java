package evaluator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import config.IConfigurer;

public abstract class ConcernReader {
	IConfigurer config;
	
	public ConcernReader(IConfigurer config)
	{
		this.config = config;
	}
	
	public HashMap<String, Set<String>> getConcernList(boolean getMethod)
	{
		HashMap<String, Set<String>> cccs = new HashMap<String, Set<String>>();		
		try {
			for (String path: config.getConcernInputPaths())
			{
				Scanner s = new Scanner(new File(path));
	
				while (s.hasNextLine()) {
					String line = s.nextLine();
					String[] parts = line.split("#");
					String concernName = parts[0];
					String calledMethodsString = parts[1];
					String callingMethodsString;
					if(parts.length > 2)
						callingMethodsString = parts[2];
					else
						callingMethodsString = "";
					String[] calledMethods = calledMethodsString.split(";");
					String[] callingMethods = callingMethodsString.split(";");
	
					Set<String> classNames = new HashSet<String>();
					classNames.addAll(getNames(calledMethods, getMethod));
					classNames.addAll(getNames(callingMethods, getMethod));
	
					cccs.put(concernName, classNames);
				}
				s.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return cccs;
	}
	
	protected List<String> getNames(String[] calledMethods, boolean getMethod) {
		List<String> result = new ArrayList<String>(calledMethods.length);
		for (int i = 0; i < calledMethods.length; i++) {
			if (!"".equals(calledMethods[i]))
				if(getMethod)
					result.add(pathToMethodName(config.convertPathName((calledMethods[i]))));
				else
					result.add(pathToFileName(config.convertPathName((calledMethods[i]))));
		}
		return result;
	}
	
	public String pathToFileName(String path)
	{
		String result = path;
		if (result.indexOf('$') > 0) {
			result = result.substring(0, result.indexOf("$"));
		}
		else {
			result = result.substring(0, result.lastIndexOf("."));
		}
		result = result.replaceAll("\\.", "/");
		result += ".java";
		return result;
	}
	
	public String pathToMethodName(String path, boolean filePlusColonFormat)
	{
		String result = path;
		
		if(filePlusColonFormat) {
			result = result.substring(0, result.lastIndexOf("."));
			result = result.replaceAll("\\.", "/");
			result += ".java:";
			String theMethodName = null;
			if (path.lastIndexOf(':') > 0) {
				theMethodName = path.substring(path.lastIndexOf(".") + 1,
						path.lastIndexOf(":"));
			} else {
				theMethodName = path.substring(path.lastIndexOf(".") + 1);
			}
			result += theMethodName;
		}
		else {
			if (path.lastIndexOf(':') > 0) {
				result = result.substring(0, result.lastIndexOf(':'));
			}
		}		
		return result;
	}
	
	public String pathToMethodName(String path)
	{
		return pathToMethodName(path, false);
	}
}
