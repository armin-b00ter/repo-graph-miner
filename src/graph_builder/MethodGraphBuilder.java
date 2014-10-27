package graph_builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;

public class MethodGraphBuilder extends GraphBuilder {
	
	@Override
	public void makeGraph(String outputFileName)
	{
		// the graph creation code that uses extractItem.
		System.out.println("Fetching Log Entries ...");
		ArrayList<ArrayList<String[]>> changedPackages = new ArrayList<>();
		ArrayList<ChangedItem> deleted = new ArrayList<>();
		ArrayList<ChangedItem> changedItems =  extractItems(changedPackages, deleted);
		System.out.println("Fetching Change Methods Finished ...");
		System.out.println("Making Initial Graph ...");
		
		long loopRevision = changedItems.get(0).revision; // we shouldent give this from config because we want to work on changedItems and maybe a some start revision doesnt have ch
		long lastRevision = changedItems.get(changedItems.size() - 1).revision;
		long tempRevision = loopRevision;
		int pack = 0;
		
		while (loopRevision <= lastRevision && changedItems.size() > 0) {
			System.out.println("revision: " + loopRevision + " -- last: " + lastRevision);
			
			//System.out.println("Packages");
			while(pack < loopRevision) {
				//System.out.println(changedPackages.size() + ">:P" + pack);
				graph.updateKeys(changedPackages.get(pack));
				pack++;
			}
			
			manageDeleted:
				for (Iterator<ChangedItem> it = deleted.iterator(); it.hasNext();) {
					ChangedItem item = it.next();
					System.out.println("I am in manageDeleted++++++++++++++++++++" + item.name + item.revision);
					tempRevision = item.revision;
					if(tempRevision > loopRevision)
						break manageDeleted;
					
					if(item.action == 'D') {
						graph.deleteNode(item.name);
						it.remove();
					}
				}
			
			//System.out.println("changeKeyLoop");
			changeKey:
				for (Iterator<ChangedItem> it = changedItems.iterator(); it.hasNext();) {
					ChangedItem item = it.next();
					tempRevision = item.revision;
		
					if(tempRevision != loopRevision)
						break changeKey;
						
					if (item.action == 'R') {
						graph.changeKey(item.copyFromPath, item.name);
						it.remove();
					}
				}
			
			//System.out.println("deleteKeyLoop");
			deleteKey:
				for (Iterator<ChangedItem> it = changedItems.iterator(); it.hasNext();) {
					ChangedItem item = it.next();
					tempRevision = item.revision;
					
					if(tempRevision != loopRevision)
						break deleteKey;
					
					if(item.action == 'D') {
						graph.deleteNode(item.name);
						it.remove();
					}
				}
			
			//System.out.println("incrementEdgeLoop");
			incrementEdge:
				for (Iterator<ChangedItem> it = changedItems.iterator(); it.hasNext();) {
					ChangedItem item1 = it.next();
					tempRevision = item1.revision;
					
					if(tempRevision != loopRevision)
						break incrementEdge;
					
					long temp2;
					innerLoop:
						for(int i = 0; i < changedItems.size(); i++) {
							ChangedItem item2 = changedItems.get(i);
							temp2 = item2.revision;
							//System.out.println("name1: " + item2.name + " " + temp2 + " name2: " + item1.name + " " + tempRevision);
							
							if(temp2 != tempRevision)
								break innerLoop;
							graph.incrementEdge(item1.name, item2.name, 1);
						}
					it.remove();
				}
				
			loopRevision = tempRevision;
		}
		
		//update rest of packages
		while(pack < changedPackages.size()) {
			System.out.println("Rest of update");
			graph.updateKeys(changedPackages.get(pack));
			pack++;
		}
		
		
		for (Iterator<ChangedItem> it = deleted.iterator(); it.hasNext();) {
			ChangedItem item = it.next();
			System.out.println("I am in manageDeleted++++++++++++++++++++" + item.name + item.revision);
			
			if(item.action == 'D') {
				graph.deleteNode(item.name);
				it.remove();
			}
		}
 		
    	graph.save(outputFileName, false);
    	System.out.println("Making Initial Finished ...");
	}
	
	private void manageFile(File file, long currentRevision, ArrayList<ChangedItem> changeSet) {
		System.out.println("in manageFIle");
		File myFile = file;
		File empty = new File("C:\\Users\\vaio\\repo-graph-miner\\src\\Empty.java");
		FileOutputStream output;
		
		try {
			File result = new File("result.txt");
			output = new FileOutputStream(result);
			MySVNDiffGenerator diffjReader = new MySVNDiffGenerator();
			diffjReader.myDisplayFileDiff(myFile, empty, output);
			java.util.Scanner fileReader = new java.util.Scanner(result);
			
			while(fileReader.hasNext()) {
				
				String line = fileReader.nextLine();
				System.out.println(line);
				int colonPos = line.lastIndexOf(':');
				String changeDescription = line.substring(0, colonPos);
				if(changeDescription.startsWith("method removed")) {
					String methodName = line.substring(colonPos + 2);
					String fullName = methodName.replaceAll(" ", "");  
					ChangedItem item = new ChangedItem(fullName, 'D', "", currentRevision);
					changeSet.add(item);
				}
			}
			result.delete();
			System.out.println("end of manageFile");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	
	protected ArrayList<ChangedItem> extractItems(ArrayList<ArrayList<String[]>> packages, ArrayList<ChangedItem> deleted) {
		ArrayList<ChangedItem> ret = new ArrayList<ChangedItem>();
		ArrayList<String[]> deletedFiles = new ArrayList<>();
		ArrayList<String[]> revPackages;
		
		 
		
		String url = config.getURL();
		String name = config.getUserName();
		String password = config.getPassword();
		String localUrl = config.getLocalURL();//repository root directory path
		
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		SVNLookClient lookClient = new SVNLookClient(authManager, null);
		MySVNDiffGenerator diffGenerator = new MySVNDiffGenerator(this.config);
		lookClient.setDiffGenerator(diffGenerator);
		
		for(int revision=config.getStartRevision(); revision<=config.getEndRevision(); revision++)
		{
			System.out.println("Getting changed methods for revision " + revision);
			try 
			{
				PipedOutputStream out = new PipedOutputStream();
				PipedInputStream in = new PipedInputStream(out);
				BufferedReader b = new BufferedReader(new InputStreamReader(in));
				revPackages = new ArrayList<>();
				packages.add(revPackages);
				DiffJParser diffJParser = new DiffJParser(ret, deletedFiles, revPackages, this.svnRepository, revision);
				DiffJReader diffJReader = new DiffJReader(b, diffJParser);
				new Thread(diffJReader).start();
				
				try 
				{
					lookClient.doGetDiff(new File(localUrl), SVNRevision.create(revision), true, true, false, out);
				} catch (SVNException e) {
					System.out.println("The url probably does not exist in revision " + revision);
					e.printStackTrace();
					System.exit(0);
				}
				diffJReader.stop();
				while(!diffJReader.hasStopped)
				{
					
				}
					//System.out.print(".");
				//System.out.println('\n');
				b.close();
				in.close();
				out.close();
	
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("*********************************");
		}
		
		for(int i = 0; i < deletedFiles.size(); i++) {
			String[] pair = deletedFiles.get(i);
			long rev = Long.parseLong(pair[1]);
			File myFile = new File("deleted.java");
			FileOutputStream deletedFile;
			try {
				deletedFile = new FileOutputStream(myFile);					
				this.svnRepository.getFile(pair[0], rev, null, deletedFile);
				this.manageFile(myFile, rev + 1, deleted);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}



	@Override
	protected ArrayList<ChangedItem> extractItems() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}




class DiffJReader implements Runnable {
	private boolean running;
	boolean hasStopped;
	private BufferedReader reader;
	private DiffJParser parser;
	

	public DiffJReader(BufferedReader reader, DiffJParser parser) {
		this.reader = reader;
		this.parser = parser;
		this.running = true;
		this.hasStopped = false;
	}

	public void run() {
		while (this.running) { 
			
			try {
				if (this.reader.ready()) {
					String line = this.reader.readLine();
					if (line != null) {
						try {
							this.parser.parseLine(line);
						} catch (SVNException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
		this.hasStopped = true;
	}

	public void stop() {
		this.running = false;
	}
}

class DiffJParser {
//	private String currentPath;
	private ArrayList<ChangedItem> changeSet;
	private ArrayList<String[]> revPackages;
	private ArrayList<String[]> deletedFiles;
	private SVNRepository svnRepository;
	private long currentRevision;
	public DiffJParser(ArrayList<ChangedItem> changeSet, ArrayList<String[]> deletedFiles, ArrayList<String[]> revPackages, SVNRepository svnRepository, long revision) {
		this.changeSet = changeSet;
		this.svnRepository = svnRepository;
		this.currentRevision = revision;
		this.revPackages = revPackages;
		this.deletedFiles = deletedFiles;
	}
	
	

	public void parseLine(String line) throws SVNException, FileNotFoundException {
			if (!line.isEmpty()) {
				try {
					if (line.startsWith("package renamed")) {
						String[] parts1 = line.split("from");
						String[] parts2 = parts1[1].split("to");
						String[] packages = {parts2[0].replaceAll(" ", ""), parts2[1].replaceAll(" ", "")};
						revPackages.add(packages);
					}
					else {
						int colonPos = line.lastIndexOf(':');
						String changeDescription = line.substring(0, colonPos);
						String methodOrFileName = line.substring(colonPos + 2);
						//System.out.println(line);
						//remove
						if(changeDescription.equals("method removed") || changeDescription.equals("constructor removed")) {
							String fullName = methodOrFileName.replaceAll(" ", "");  
							ChangedItem item = new ChangedItem(fullName, 'D', "", currentRevision);
							this.changeSet.add(item);
						}
						//add
						else if (changeDescription.equals("method added") || changeDescription.equals("constructor added")) {
							String fullName = methodOrFileName.replaceAll(" ", "");  
							ChangedItem item = new ChangedItem(fullName, 'A', "", currentRevision);
							this.changeSet.add(item);
						}
						else if (changeDescription.equals("Deleted")) {
							if(methodOrFileName.endsWith(".java")) {
//								System.out.println("I don't know");
//								java.util.Scanner sc = new java.util.Scanner(System.in);
//								int a = sc.nextInt();
							
								String[] pair = {methodOrFileName, (currentRevision - 1) + ""};
								deletedFiles.add(pair);
//								File myFile = new File("deleted.java");
//								FileOutputStream deletedFile;
//								deletedFile = new FileOutputStream(myFile);					
//								this.svnRepository.getFile(methodOrFileName, this.currentRevision - 1, null, deletedFile);
//								this.manageFile(myFile);
								System.out.println("out of manageFile");
							}
						}

						//changeKey
						else if(changeDescription.startsWith("parameter") && !changeDescription.startsWith("parameter name")) {
							String[] parts = methodOrFileName.split(">");
							String oldMethodName = parts[0].replaceFirst(" ","");
							String newMethodName = parts[1].replaceAll(" ", "");
							ChangedItem item = new ChangedItem(newMethodName, 'R', oldMethodName, currentRevision);
							this.changeSet.add(item);
						}
						//modify
						else if(changeDescription.startsWith("code")) {
							String fullName = methodOrFileName.replaceAll(" ", "");  
							ChangedItem item = new ChangedItem(fullName, 'M', "", currentRevision);
							this.changeSet.add(item);
						}
						else if (changeDescription.startsWith("throws|modifier|access|return")){
							
						}

					}
					

				}
				catch (StringIndexOutOfBoundsException e) {
					//System.err.println("There is probably no colon in the following DiffJ output line: " + line);
			}
		}
	}
	
	
}