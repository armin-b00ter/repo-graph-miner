package config;

public interface IConfigurer {
	int getFromRevision();
	int getToRevision();
	String getURL();
	boolean igonreThisPath(String path);
	String convertPathName(String path);
}
