import graph_builder.FileGraphBuilder;
import graph_builder.GraphBuilder;
import graph_builder.MethodGraphBuilder;

import java.util.HashMap;

import config.IConfigurer;
import config.JHotDrawConfigurer;
import config.TomCatConfigurer;


public class ConfigFactory {
	private HashMap<String, IConfigurer> configInstances = new HashMap<String, IConfigurer>();
	private HashMap<String, GraphBuilder> builderInstances = new HashMap<String, GraphBuilder>();
	
	public ConfigFactory()
	{
		configInstances.put("jhotdraw", new JHotDrawConfigurer());
		configInstances.put("tomcat", new TomCatConfigurer());
		
		builderInstances.put("file", new FileGraphBuilder());
		builderInstances.put("method", new MethodGraphBuilder());
	}
	
	public String getConfigurersList()
	{
		String ret = "";
		for(String key : configInstances.keySet())
			ret += "|" + key;
		return ret.substring(1);
	}
	
	public String getBuildersList()
	{
		String ret = "";
		for(String key : builderInstances.keySet())
			ret += "|" + key;
		return ret.substring(1);
	}
	
	public IConfigurer getConfigurer(String key)
	{
		if(configInstances.containsKey(key))
			return configInstances.get(key);
		return null;
	}
	
	public GraphBuilder getBuilder(String key)
	{
		if(builderInstances.containsKey(key))
			return builderInstances.get(key);
		return null;
	}
}
