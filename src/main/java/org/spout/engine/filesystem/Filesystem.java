package org.spout.engine.filesystem;

import java.io.File;

public class Filesystem {

	public static final File resourceFolder = new File("resources");
	public static final File cacheFolder = new File("cache");	
	public static final File pluginDirectory = new File("plugins");
	public static final File configDirectory = new File("config");
	public static final File updateDirectory = new File("update");
	public static final File dataDirectory = new File("data");	
	public static final File worldsDirectory = new File("worlds");
	
	
	static {
		if(!resourceFolder.exists()) resourceFolder.mkdirs();
		if(!cacheFolder.exists()) cacheFolder.mkdirs();
		if(!pluginDirectory.exists()) pluginDirectory.mkdirs();
		if(!configDirectory.exists()) configDirectory.mkdirs();
		if(!updateDirectory.exists()) updateDirectory.mkdirs();
		if(!dataDirectory.exists()) dataDirectory.mkdirs();
		if(!worldsDirectory.exists()) worldsDirectory.mkdirs();
	}
	
	
}
