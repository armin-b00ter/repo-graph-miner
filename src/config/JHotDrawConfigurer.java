package config;

public class JHotDrawConfigurer implements IConfigurer {

	String userName;
	String Password;
	int startRevision = 0;
	int endRevision = 0;//-1;//HEAD (the latest) revision
	String URL = "";
	
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
}
