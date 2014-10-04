package graph_builder;

public class ChangedItem {
	String name;
	char action;
	String copyFromPath;
	long revision;
	
	public ChangedItem(String name, char action, String copyFromPath, long revision){
		this.name = name;
		this.action = action;
		this.copyFromPath = copyFromPath;
		this.revision = revision;
	}
	
}
