package graph_builder;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class FileGraphBuilder extends GraphBuilder{

	@Override
	protected ArrayList<ChangedItem> extractItems() {
		Collection logEntries = null;
        
        String url = config.getURL();
		
		try {
            logEntries = svnRepository.log(new String[] {""}, null,
            		config.getStartRevision(), config.getEndRevision(), true, true);

        } catch (SVNException svne) {
            System.out.println("error while collecting log information for '"
                    + url + "': " + svne.getMessage());
            System.exit(1);
        }
		
		ArrayList<ChangedItem> ret = handleLogActions(logEntries, graph);
		return ret ;
	}
	
	 private ArrayList<ChangedItem> handleLogActions(Collection logEntries, Graph graph){
		 ArrayList<ChangedItem> changedItems = new ArrayList<ChangedItem>();
	    	for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
	    		SVNLogEntry logEntry = (SVNLogEntry) entries.next();
	            //displaying all paths that were changed in that revision; changed path information is represented by SVNLogEntryPath.
	            if (logEntry.getChangedPaths().size() > 0) {
	            	Set changedPathsSet = logEntry.getChangedPaths().keySet();
	                for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
	                    //obtains a next SVNLogEntryPath
	                    SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());	 
	                    String path = null, copyPath = null;
	                    if(entryPath.getCopyPath() == null){
		                    if(config.igonreThisPath(entryPath.getPath())){
			                    path = config.convertPathName(entryPath.getPath());
			                    changedItems.add(new ChangedItem(path, entryPath.getType(), copyPath));	                    		
		                		changedPaths.remove();
		                    }
	                    }else{	                    	
                    		if(config.igonreThisPath(entryPath.getPath()) && config.igonreThisPath(entryPath.getCopyPath())){
			                    path = config.convertPathName(entryPath.getPath());
			                    copyPath = config.convertPathName(entryPath.getCopyPath());
			                    changedItems.add(new ChangedItem(path, entryPath.getType(), copyPath));	                    		
		                		changedPaths.remove();
	                    	}
	                    }	                    
	                }
	            }
	            System.out.println("Revision " + logEntry.getRevision() + " Finished.");
	    	}	    	
	    	return changedItems;
	    }
	
	
	 public static void printLog(Collection logEntries){
	    	long counter = 0;
	    	for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
	    		counter++;
	            SVNLogEntry logEntry = (SVNLogEntry) entries.next();
	            System.out.println("---------------------------------------------");
	            System.out.println("revision: " + logEntry.getRevision());
	            System.out.println("author: " + logEntry.getAuthor());
	            System.out.println("date: " + logEntry.getDate());
	            System.out.println("log message: " + logEntry.getMessage());
	            if (logEntry.getChangedPaths().size() > 0) {
	                System.out.println();
	                System.out.println("changed paths:");
	                Set changedPathsSet = logEntry.getChangedPaths().keySet();
	                for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
	                    SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
	                    System.out.println(" "
	                            + entryPath.getType()
	                            + "	"
	                            + entryPath.getPath()
	                            + ((entryPath.getCopyPath() != null) ? " (from "
	                                    + entryPath.getCopyPath() + " revision "
	                                    + entryPath.getCopyRevision() + ")" : ""));
	                }
	            }
	        }
	    	System.out.println("Counter : " + counter);
	    }
	
}
