package graph_builder;

import java.util.ArrayList;

import org.tmatesoft.svn.core.wc.SVNClientManager;

import config.IConfigurer;

public abstract class GraphBuilder {
	Graph graph;
	IConfigurer config;
	SVNClientManager svnClient;
	
	public GraphBuilder()
	{
		graph = new Graph();
		this.config = null;
	}
	
	public GraphBuilder(IConfigurer config)
	{
		graph = new Graph();
		this.config = config;
	}
	
	protected abstract ArrayList<ChangeItem> extractItem(int revisionNumber);
	
	public void makeGraph(int revisionFrom, int revisionTo)
	{
		// the graph creation code that uses extractItem.
	}	
}
