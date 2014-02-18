package graph_builder;

import java.sql.Date;
import java.util.ArrayList;

public abstract class GraphBuilder {
	Graph graph;
	
	public GraphBuilder()
	{
		graph = new Graph();
	}
	
	protected abstract ArrayList<ChangeItem> extractItem(int revisionNumber);
	
	public void makeGraph(int revisionFrom, int revisionTo)
	{
		// the graph creation code that uses extractItem.
	}
	
	public void makeGraph(Date from, Date to)
	{
		// the graph creation code that uses extractItem.
	}
	
	
}
