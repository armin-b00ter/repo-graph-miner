package graph_builder;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Graph {
	
	HashMap<String, HashMap<String, Long>> edges= new HashMap<String, HashMap<String,Long>>();

	
	private void addEdge(String x, String y, long wieght) {
		String first, second;
		if (x.compareTo(y) < 0){
			first = x;
			second = y;
		}else{
			first = y;
			second = x;
		}
		
		HashMap<String, Long> temp;
		if(edges.containsKey(first)){
			temp = edges.get(first);			
		}	else{
			temp = new HashMap<String, Long>();
		}
		temp.put(second, wieght);
		edges.put(first, temp);
	}
	
	//DeleteNode
		public void deleteNode(String key){
			if(edges.containsKey(key)){
				edges.remove(key);
			}
			
			ArrayList<String> x_keys = new ArrayList<String>(edges.keySet());
			for(String x2 : x_keys)
			{
				ArrayList<String> y_keys = new ArrayList<String>(edges.get(x2).keySet());
				for(String y : y_keys)
				{
					if(y.equals(key)){
						edges.get(x2).remove(y);
					}
				}
			}		
		}
		
		
		//ChangeKey
		public void changeKey(String oldKey, String newKey){
			
			if(edges.containsKey(oldKey)){
				HashMap<String, Long> temp = edges.get(oldKey);
				edges.put(newKey, temp);
				edges.remove(oldKey);
			}
			
			ArrayList<String> x_keys = new ArrayList<String>(edges.keySet());
			for(String x : x_keys)
			{
				ArrayList<String> y_keys = new ArrayList<String>(edges.get(x).keySet());
				for(String y : y_keys)
				{
					if( y.equals(oldKey)){
						long wieght = edges.get(x).get(y);
						edges.get(x).put(newKey, wieght);
						edges.get(x).remove(oldKey);
					}
				}
			}
			
			
		}
		
		public void updateKeys(ArrayList<String[]> keys) {
			ArrayList<String> x_keys = new ArrayList<String>(edges.keySet());
			System.out.println("in the updateKeys" + keys.size());
			
			for(int i = 0; i < keys.size(); i++) {
				System.err.println("I am in for updateKeys with size: " + keys.size());
				String oldKey = (keys.get(i))[0];
				String newKey = (keys.get(i))[1];
				for(String x : x_keys) {
					if(x.startsWith(oldKey)) {
						String newNodeName = x.replaceFirst(oldKey, newKey);
						changeKey(x, newNodeName);
					}
				}
			}
		}
	
	
	public Long getEdgeWieght(String x, String y){
		String first, second;
		if (x.compareTo(y) < 0){
			first = x;
			second = y;
		}else{
			first = y;
			second = x;
		}
		
		if (edges.containsKey(first) && edges.get(first).containsKey(second)){
			return edges.get(first).get(second);
		}
		
		return null;
	}
	
	
	public void incrementEdge(String x, String y, long value) {
		
		String first, second;
		if (x.compareTo(y) < 0){
			first = x;
			second = y;
		}else{
			first = y;
			second = x;
		}
		
//		System.out.println(first + " " + second);
		long newWieght = 1;
		if (edges.containsKey(first) && edges.get(first).containsKey(second)){
			newWieght = edges.get(first).get(second) + value;
//			System.out.println("contains" + newWieght);
		}
		addEdge(first, second, newWieght);
	}
	
	public void save(String fileAddr){
		try{
		FileOutputStream fout = new FileOutputStream(fileAddr);
		PrintStream ps = new PrintStream(fout);
		
		ArrayList<String> x_keys=new ArrayList<String>(edges.keySet());
		Collections.sort(x_keys);
		
		for(String x: x_keys)
		{
			ArrayList<String> y_keys=new ArrayList<String>(edges.get(x).keySet());
			Collections.sort(y_keys);
			for(String y: y_keys)
			{
				ps.println(x + "\t" + y + "\t" + edges.get(x).get(y));
				
			}
		}
		ps.flush();
		ps.close();
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
