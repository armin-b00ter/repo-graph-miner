//package graph_builder;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.Scanner;
//
//import org.incava.diffj.DiffJ;
//
//public class MyCode extends GraphBuilder{
//	
//	
//	@Override
//	protected ArrayList<ChangedItem> extractItems() {
//		FileGraphBuilder fg = new FileGraphBuilder();
//		ArrayList<ChangedItem> changedFiles = fg.extractItems();
//		
//		for(int i = 0; i < changedFiles.size(); i++) {
//			ChangedItem item = changedFiles.get(i);
//			if(item.action == 'M') {
//				//svnRepository.getFile(changedFiles.get(i).name, , arg2, flie1.java)
//				//svnRepository.getFile(changedFiles.get(i).name, , arg2, file2.java)
//			}
//		}
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	protected ArrayList<ChangedItem> DiffParser(String from, String to) {
//		FileOutputStream stream;
//		try {
//			File file = new File("diff-resut.txt");
//			stream = new FileOutputStream(file);
//			String[] args = {from, to, "brief"};
//			DiffJ dj = new DiffJ(args, stream);
//			Scanner reader = new Scanner(file);
//			String line;
//			while(reader.hasNext()) {
//				line = reader.nextLine();
//				if(line.startsWith("[0-9]")) {
//					String[] keys = (line.split(" "));
//					if(isChange(keys[1])) {
//						
//					}
//					else if (isRemove(keys[1], keys[2])) {
//						
//					}
//					else if (isAdd(keys[1], keys[2])) {
//						
//					}
//					
//				}
//				
//			}
//		
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//brief doroste?
//		
//	}
//	
//	private boolean isChange(String key) {
//		if (key.equals("parameter") || key.equals("throws") || key.equals("access") || key.equals("code") || key.equals("return"))
//			return true;
//		else 
//			return false;
//	}
//	
//	private boolean isRemove(String key, String action) {
//		//should we consider constructors??
//		if(key.equals("method") && action.equals("removed"))
//			return true;
//		else 
//			return false;
//		
//	}
//	
//	private boolean isAdd(String key, String action) {
//		if(key.equals("method") && action.equals("added"))
//			return true;
//		else 
//			return false;
//		
//	}
//}
