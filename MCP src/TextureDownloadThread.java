package net.minecraft.src;

import java.io.File;
import java.io.FileOutputStream;
import org.apache.commons.io.FileUtils;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class TextureDownloadThread extends Thread {
	private String url;
	private String name;
	private boolean completed = false;
	public TextureDownloadThread(String url, String identifier) {
		this.url = url;
		this.name = identifier;
	}
	
	public void run() {
		File directory = new File(BukkitContribCache.getTextureCacheDirectory(), "temp");
		if (!directory.exists()) {
			directory.mkdir();
		}
		File tempDir = new File(directory, "downloading");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
		File download = new File(tempDir, name);
		try {
			URL texture = new URL(url);
	        ReadableByteChannel rbc = Channels.newChannel(texture.openStream());
	        FileOutputStream fos = new FileOutputStream(download);
	        fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			System.out.println("Downloading: " + name);
			fos.close();
			FileUtils.moveFile(download, new File(directory, download.getName()));
		}
		catch (Exception e) {
			download.delete();
			e.printStackTrace();
		}
		
		completed = true;
	}
	
	public boolean isDownloadComplete() {
		return completed;
	}
	
	public String getUrl() {
		return url;
	}

}
