package graph_builder;

import java.util.ArrayList;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import config.IConfigurer;

public abstract class GraphBuilder {
	Graph graph;
	IConfigurer config;
	SVNRepository svnRepository;
	
	public GraphBuilder()
	{
		graph = new Graph();
		this.config = null;
		
		setupConfiguration(config);
	}

	public GraphBuilder(IConfigurer config)
	{
		graph = new Graph();
		this.config = config;
		
		setupConfiguration(config);
	}
	
	private void setupConfiguration(IConfigurer config){
		String url = config.getURL();
        String name = config.getUserName();
        String password = config.getPassword();
        
        setupLibrary();
        SVNRepository svnRepository = null;
        try {
        	svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        } catch (SVNException svne) {
            System.err.println("error while creating an SVNRepository for the location '" + url + "': " + svne.getMessage());
            System.exit(1);
        }

        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        svnRepository.setAuthenticationManager(authManager);
	}
	
	protected abstract ArrayList<ChangedItem> extractItem(int revisionNumber);
	
	
	public void makeGraph(int revisionFrom, int revisionTo)
	{
		// the graph creation code that uses extractItem.
		for(int i = revisionFrom; i <= revisionTo; i++){
			 ArrayList<ChangedItem> changedItems =  extractItem(i);	
			for(ChangedItem item : changedItems){
				if(item.action == 'A' || item.action == 'R'){
                	if(item.copyFromPath != null){
                		graph.changeKey(item.copyFromPath, item.name);                		
                		changedItems.remove(item);
                	}
                }
			}
			
			for(ChangedItem item : changedItems){
				if(item.action == 'D'){
	                	graph.deleteNode(item.name);	                	
	                	changedItems.remove(item);
	                }			
            }
			
        	for(ChangedItem item1 : changedItems){
        		for(ChangedItem item2 : changedItems){
                    graph.incrementEdge(item1.name, item2 .name, 1);
        		}
        	}			
        	graph.save("StartReversion "+ revisionFrom+ " EndRevision "+ revisionTo+".txt");
		}
	}
	
	
	private static void setupLibrary() {
        //For using over http:// and https://
        DAVRepositoryFactory.setup();

        // For using over svn:// and svn+xxx:
        SVNRepositoryFactoryImpl.setup();
        
        //For using over file:///
        FSRepositoryFactory.setup();
    }

	public SVNRepository getSvnRepository() {
		return svnRepository;
	}

	public void setSvnRepository(SVNRepository svnRepository) {
		this.svnRepository = svnRepository;
	}
}
