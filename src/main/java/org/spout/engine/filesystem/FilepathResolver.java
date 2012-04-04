package org.spout.engine.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import org.spout.api.resource.ResourcePathResolver;

public class FilepathResolver implements ResourcePathResolver  {

	protected final String directory;
	
	public FilepathResolver(String path){
		this.directory = path;
	}
	
	
	@Override
	public boolean existsInPath(String file, String path) {
		File f = new File(path + File.pathSeparator + file);
		return f.exists();
	}

	@Override
	public boolean existsInPath(URI path) {
		return this.existsInPath(path.getPath(), directory + File.pathSeparator + path.getHost());
	}

	@Override
	public FileInputStream getStream(String file, String path) {
		try {
			return new FileInputStream(new File(path + File.pathSeparator + file));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public FileInputStream getStream(URI path) {
		return this.getStream(path.getPath(), directory + File.pathSeparator + path.getHost());
	}

}
