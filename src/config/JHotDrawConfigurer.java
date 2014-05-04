package config;

public class JHotDrawConfigurer implements IConfigurer {

	String userName;
	String Password;
	int startRevision = 1;
	int endRevision = 267;//-1;//HEAD (the latest) revision
	String URL = "https://svn.code.sf.net/p/jhotdraw/svn/";	
	String localURL="D:\\Workspace\\repos\\svn";//repository root directory path
	
	@Override
	public int getStartRevision() {
		return startRevision;
	}

	@Override
	public int getEndRevision() {
		return endRevision;
	}

	@Override
	public String getURL() {
		return URL;
	}

	@Override
	public boolean igonreThisPath(String path) {
		if(path.contains("src") == false || path.contains("test") == true){
			return true;
		}
		return false;
	}

	@Override
	public String convertPathName(String path) {
		if(!path.contains("src/")){
			return path;
		}
		
		String ret = path.split("src/")[1];
		if(ret.contains("jhotdraw/"))		
			ret = ret.split("jhotdraw/")[1]; 
		return ret;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getPassword() {
		return Password;
	}

	@Override
	public String getLocalURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getConcernInputPaths() {
		String[] ret = {
				"case_study\\jhotdraw\\cross_jhd_dynamo.txt",
				"case_study\\jhotdraw\\cross_jhd_fanin.txt",
				"case_study\\jhotdraw\\cross_jhd_ident.txt"
		};
		return ret;
	}
}
