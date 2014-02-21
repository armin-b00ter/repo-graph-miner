package config;

public interface Configurer {
	int getFromRevision();
	int getToRevision();
	String getURL();
	boolean igonreThisPath(String path);
	String convertPathName(String path);
}
