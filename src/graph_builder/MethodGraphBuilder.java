package graph_builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;

public class MethodGraphBuilder extends GraphBuilder {
	
	protected ArrayList<ChangedItem> extractItems() {
		ArrayList<ChangedItem> ret = new ArrayList<ChangedItem>();
		
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
				DiffJParser diffJParser = new DiffJParser(ret);
				DiffJReader diffJReader = new DiffJReader(b, diffJParser);
				new Thread(diffJReader).start();
				
				try 
				{
					lookClient.doGetDiff(new File(localUrl), SVNRevision.create(revision), false, true, true, out);
				} catch (SVNException e) {
					System.out.println("The url probably does not exist in revision " + revision);
					e.printStackTrace();
					System.exit(0);
				}
				diffJReader.stop();
				while(!diffJReader.hasStopped) System.out.print(".");
				b.close();
				in.close();
				out.close();
	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;
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
						this.parser.parseLine(line);
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

	public DiffJParser(ArrayList<ChangedItem> changeSet) {
		this.changeSet = changeSet;
	}

	public void parseLine(String line) {
			if (!line.isEmpty()) {
				try {
					System.out.println(line);
					int colonPos = line.lastIndexOf(':');
					String changeDescription = line.substring(0, colonPos);
					String methodName = line.substring(colonPos + 2);
					if (!changeDescription.equals("method removed")) {
						if (changeDescription.startsWith("parameter")) {
							System.out.println(line);
							String[] parts = methodName.split(">");
							String oldMethodName = parts[0].trim().replaceFirst("CH.ifa.draw.","").replaceFirst("org.jhotdraw.","").replaceAll(" ", "");
							String newMethodName = parts[1].trim().replaceFirst("CH.ifa.draw.","").replaceFirst("org.jhotdraw.","").replaceAll(" ", "");
							ChangedItem item = new ChangedItem(newMethodName, 'A', oldMethodName);
							this.changeSet.add(item);		// Still record this as a method change
						}
						else {
							String fullName = methodName.replaceFirst("CH.ifa.draw.","").replaceFirst("org.jhotdraw.","").replaceAll(" ", "");  // Remove spaces to match the info in the ccc file
							ChangedItem item = new ChangedItem(fullName, 'A', "");
							this.changeSet.add(item);
						}
					}
				}
				catch (StringIndexOutOfBoundsException e) {
					System.err.println("There is probably no colon in the following DiffJ output line: " + line);
			}
		}
	}
}