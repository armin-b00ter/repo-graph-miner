package graph_builder;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.incava.diffj.DiffJ;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNGNUDiffGenerator;
import org.tmatesoft.svn.core.internal.wc.SVNFileUtil;

import config.IConfigurer;

public class MySVNDiffGenerator extends DefaultSVNGNUDiffGenerator {
	
	IConfigurer config;
	public MySVNDiffGenerator(IConfigurer config)
	{
		this.config = config;
	}
	
	@Override
	public void displayFileDiff(String path, File file1, File file2,
			String rev1, String rev2, String mimeType1, String mimeType2,
			OutputStream result) throws SVNException {
		
		if (!config.igonreThisPath(path) && path.endsWith(".java")) 
		{
			PrintStream printStream = new PrintStream(result);
	
			File myFile1 = file1;
			File myFile2 = file2;
			
			if (file1 == null) {
				myFile1 = SVNFileUtil.createTempFile("svn.", ".tmp");
			}
			if (file2 == null) {
				myFile2 = SVNFileUtil.createTempFile("svn.", ".tmp");
			}
			// myFile1.deleteOnExit();
			// myFile2.deleteOnExit();
	
	//			System.out.println("PATH: " + path);
	
			// System.out.println("Running DiffJ");
			
			int revision = Integer.parseInt(rev2.split(" ")[1].replace(")", ""));
	
			try {
				DiffJ dj = new DiffJ(new String[] {
						"--source",
						"1.5",
						"--brief",
						myFile1.toString(),
						myFile2.toString()
				}, printStream);
	
				// if (dj.exitValue != 0 && dj.exitValue != 1) {
				if (dj.exitValue == -1) {
					/*
					 * SVNErrorMessage err = SVNErrorMessage.create(
					 * SVNErrorCode.EXTERNAL_PROGRAM, "''{0}'' returned {1}",
					 * new Object[] { "DiffJ", String.valueOf(dj.exitValue) });
					 * SVNErrorManager.error(err, SVNLogType.DEFAULT);
					 */
					System.err.println("DiffJ returned with " + dj.exitValue
							+ ". Please check.");
				}
			} catch (Exception e) {
				// Catch all exceptions to make sure application does not quit
				// just because of an error in DiffJ for one certain revision...
				e.printStackTrace();
			}
	
			printStream.println();
			myFile1.delete();
			myFile2.delete();
		}
	}

	@Override
	public void displayPropDiff(String path, SVNProperties baseProps,
			SVNProperties diff, OutputStream result) throws SVNException {
		// We are not interested in property changes
	}
	
	@Override
	public void displayAddedDirectory(String path, String rev1, String rev2) {
		/*System.out.println("I AM HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("PATH IS: " + path);
		System.out.println(this.isDiffCopied());*/
	}
}
