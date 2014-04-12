package graph_builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.admin.SVNLookClient;

import config.IConfigurer;

public class MethodGraphBuilder extends GraphBuilder {

	@Override
	protected ArrayList<ChangedItem> extractItem(int revisionNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	
	private Pair<ChangeSetList, EntityIDMap> extractAndReinforce(
			List<SVNLogEntry> logEntries, IConfigurer config) {
		// Do this just to be sure (e.g. when GetLogEntries has been skipped)
		GetLogEntries.setupLibrary();
		
		EntityIDMap fileIDs = new EntityIDMap();
		
		ChangeSetList changeSets = new ChangeSetList();

		// The following is actually only needed for method-level mining, but is
		// kept here to maximize code re-use
		String url = config.getURL();
		String name = config.getUserName();
		String password = config.getPassword();

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		//-------------------------- ta inja tuye Graph builder ---------------------------
		SVNLookClient lookClient = new SVNLookClient(authManager, null);

		MySVNDiffGenerator diffGenerator = new MySVNDiffGenerator();
		lookClient.setDiffGenerator(diffGenerator);
		// End of method-level-specific part

		int counter = 1;

		for (Iterator<SVNLogEntry> entries = logEntries.iterator(); entries.hasNext();) {
			ChangeSet changeSet = null;

			SVNLogEntry logEntry = entries.next();
			
				// Append items to a new set
				changeSet = new ChangeSet();
				changeSet.transactionNo = counter;
				counter++;
				changeSet.revisions.add(logEntry.getRevision());
				changeSets.add(changeSet);

			fillChangeSet(changeSet, logEntry, fileIDs, lookClient);


		}
		////////////////////////////////
		fileIDs.createReverseMap();		// Allow reverse lookup
		////////////////////////////////

		return Tuple.from(changeSets, fileIDs);
	}
	
	
	protected List<Integer> fillChangeSet(Set<Integer> changeSet, SVNLogEntry logEntry,
			EntityIDMap entityIDs, SVNLookClient lookClient) {
		long revision = logEntry.getRevision();

		System.out.println("Getting changed methods for revision " + revision);
		String url = config.getURL();
		String localUrl = config.getLocalURL();//repository root directory path
		
		try {
			PipedOutputStream out = new PipedOutputStream();
			PipedInputStream in = new PipedInputStream(out);
			BufferedReader b = new BufferedReader(new InputStreamReader(in));
			DiffJParser diffJParser = new DiffJParser(changeSet, entityIDs);
			DiffJReader diffJReader = new DiffJReader(b, diffJParser);
			new Thread(diffJReader).start();

					try {
						lookClient.doGetDiff(new File(localUrl), SVNRevision.create(revision), false, true, true, out);
					} catch (SVNException e) {
						System.out
								.println("The url probably does not exist in revision "
										+ revision);
						e.printStackTrace();
					}
			diffJReader.stop();
			while(!diffJReader.hasStopped) System.out.print(".");
			b.close();
			in.close();
			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Should be changed if we want to keep track of deleted files
		return null;

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
//		private String currentPath;
		private Set<Integer> changeSet;
		private EntityIDMap entityIDs;

		public DiffJParser(Set<Integer> changeSet, EntityIDMap entityIDs) {
			this.changeSet = changeSet;
			this.entityIDs = entityIDs;
		}

		public void parseLine(String line) {
				if (!line.isEmpty()) {
					try {
//						System.out.println(line);
						int colonPos = line.lastIndexOf(':');
						String changeDescription = line.substring(0, colonPos);
						String methodName = line.substring(colonPos + 2);
						if (!changeDescription.equals("method removed")) {
							if (changeDescription.startsWith("parameter")) {
								System.out.println(line);
								String[] parts = methodName.split(">");
								String oldMethodName = parts[0].trim().replaceFirst("CH.ifa.draw.","").replaceFirst("org.jhotdraw.","").replaceAll(" ", "");
								String newMethodName = parts[1].trim().replaceFirst("CH.ifa.draw.","").replaceFirst("org.jhotdraw.","").replaceAll(" ", "");
								this.entityIDs.renameFile(oldMethodName, newMethodName);  // Take account of the signature change (keep using the same id)
								if (!entityIDs.entityIDMap.containsKey(newMethodName)) {
									System.err.println("EntityID map was expected to contain " + newMethodName + " but it didn't.");
								}
								int id = this.entityIDs.getIDForEntity(newMethodName);
								this.changeSet.add(id);		// Still record this as a method change
							}
							else {
								String fullName = methodName.replaceFirst("CH.ifa.draw.","").replaceFirst("org.jhotdraw.","").replaceAll(" ", "");  // Remove spaces to match the info in the ccc file
								int id = this.entityIDs.getIDForEntity(fullName);
								this.changeSet.add(id);
							}
						}
					}
					catch (StringIndexOutOfBoundsException e) {
						System.err.println("There is probably no colon in the following DiffJ output line: " + line);
					}
				}
//			}
		}
	}

	class ChangeSet extends HashSet<Integer> {
		private static final long serialVersionUID = 1L;
		protected int transactionNo;
		protected List<Long> revisions = new ArrayList<Long>();
	}
}
