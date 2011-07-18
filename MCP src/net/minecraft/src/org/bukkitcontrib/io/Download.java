package org.bukkitcontrib.io;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class Download {
	protected final String filename;
	protected final File directory;
	protected final String url;
	protected final Runnable action;
	public Download(String filename, File directory, String url, Runnable action) {
		this.filename = filename;
		this.directory = directory;
		this.url = url;
		this.action = action;
	}
	
	public File getTempFile() {
		return new File(FileUtil.getTempDirectory(), filename);
	}
	
	public boolean isDownloaded() {
		return (new File(directory, filename)).exists();
	}
	
	public void move() {
		File current = getTempFile();
		if (current.exists()) {
			File destination = new File(directory, filename);
			try {
				FileUtils.moveFile(current, destination);
			}
			catch (IOException e) {}
		}
	}
	
	public String getDownloadUrl() {
		return url;
	}
	
	public Runnable getCompletedAction() {
		return action;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Download) {
			Download temp = (Download)obj;
			return temp.filename.equals(this.filename) && temp.directory.getPath().equals(this.directory.getPath()) && temp.url.equals(this.url);
		}
		return false;
	}
}
