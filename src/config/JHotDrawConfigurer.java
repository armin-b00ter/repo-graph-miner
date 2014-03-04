package config;

public class JHotDrawConfigurer implements IConfigurer {

	int startRevision = 0;
	int endRevision = 0;//-1;//HEAD (the latest) revision
	String URL = "";
	
	@Override
	public int getStartRevision() {
		// TODO Auto-generated method stub
		return startRevision;
	}

	@Override
	public int getEndRevision() {
		// TODO Auto-generated method stub
		return endRevision;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean igonreThisPath(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String convertPathName(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getConcernInputPaths() {
		// TODO Auto-generated method stub
		return null;
	}
}
