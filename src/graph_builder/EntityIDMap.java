package graph_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class EntityIDMap implements Comparable<EntityIDMap> {

	private int entityCounter = 1;
	protected HashMap<String, Integer> entityIDMap = null;
	private HashMap<Integer, String> reverseMap = null;
	
	public EntityIDMap() {
		entityIDMap = new HashMap<String, Integer>(); 
	}
	
	public int getIDForEntity(String entity) {
		int entityID;
		
		if (!entityIDMap.containsKey(entity)) {
			entityID = this.entityCounter;
			entityIDMap.put(entity, this.entityCounter);
			this.entityCounter++;
		}
		else {
			entityID = entityIDMap.get(entity);
		}
		return entityID;
	}
	
	public String getEntityByID(int id) {
		return this.reverseGet(id);
	}
	
	public String reverseGet(int value) {
		if (this.reverseMap != null) {
			return this.reverseMap.get(value); 
		}
		else {
			throw new NullPointerException("The reverse map has not been created; do you really want me to do an O(n) lookup??");
		}
	}
	
	public void createReverseMap() {
		this.reverseMap = new HashMap<Integer, String>();
		for (Entry<String, Integer> entry : entityIDMap.entrySet()) {
//			if (entry.getValue() != 0)
				this.reverseMap.put(entry.getValue(), entry.getKey());
		}
//		this.reverseMap.put(0, "DELETED");
	}
	
	public void renameFile(String oldName, String newName) {
		if (this.entityIDMap.containsKey(oldName)) {
			int originalFileID = entityIDMap.get(oldName);
			this.entityIDMap.put(newName, originalFileID);		// Use the file ID of the original file to take account of copy information
			this.entityIDMap.remove(oldName);	// Remove the old one to avoid confusion when creating the reverse map (so now it's more like a move than a copy)
			System.out.println("Reusing ID " + originalFileID + " from " + oldName + " for copied entity " + newName);
		}
		else
			System.err.println("Trying to rename a file that does not exist: " + oldName);
	}
	
	public void renameDir(String oldName, String newName) {
		
		List<String> toChange = getAllKeysStartingWith(oldName);
		
		for (String fileName : toChange) {
//			int id = this.entityIDMap.get(fileName);
			String newFileName = fileName.replaceFirst(oldName, newName);
			/*entityIDMap.remove(fileName);		// Remove the old one...
			entityIDMap.put(newFileName, id);	// ... and add the renamed one*/
			this.renameFile(fileName, newFileName);
		}
	}

	private List<String> getAllKeysStartingWith(String prefix) {
		List<String> keys = new ArrayList<String>();
		// Warning: inefficient; would be better to recursively fetch the contents of the directory and do an operation on every file inside that directory
		for (String fileName : this.entityIDMap.keySet()) {
			if (fileName.startsWith(prefix)) {
				keys.add(fileName);
			}
		}
		return keys;
	}
	
	public List<Integer> getAllValuesForKeysStartingWith(String prefix) {
		List<Integer> values = new ArrayList<Integer>();
		for (Entry<String, Integer> entry: this.entityIDMap.entrySet()) {
			if (entry.getKey().startsWith(prefix)) {
				values.add(entry.getValue());
			}
		}
		return values;
	}
	
	
	
	/*public void deleteFile(String fileName) {
		entityIDMap.put(fileName, 0);	// Replace existing id with magic value 0
	}
	
	public void deleteDir(String dirName) {
		List<String> toChange = new ArrayList<String>();
		for (String fileName : this.entityIDMap.keySet()) {
			if (fileName.startsWith(dirName)) {
				toChange.add(fileName);
			}
		}
		for (String fileName : toChange) {
			this.deleteFile(fileName);
		}
	}*/
	
	public int compareTo(EntityIDMap o) {
		// Only needed for Tuple
		return 0;
	}

}

