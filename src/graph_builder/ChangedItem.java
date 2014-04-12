package graph_builder;

public class ChangedItem {
	String name;
	char action;
	String copyFromPath;
	
	public ChangedItem(String name, char action, String copyFromPath){
		this.name = name;
		this.action = action;
		this.copyFromPath = copyFromPath;
	}
}
