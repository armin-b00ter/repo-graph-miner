package config;

public interface IConfigurer {
	int getStartRevision();
	int getEndRevision();
	String getURL();
	boolean igonreThisPath(String path);
	String convertPathName(String path);
	String getUserName();
	String getPassword();
	String getLocalURL();//repository root directory path
	String[] getConcernInputPaths();
}
