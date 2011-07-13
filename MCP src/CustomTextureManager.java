package net.minecraft.src;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;
import java.util.HashSet;
import java.io.File;
import java.io.IOException;

public class CustomTextureManager {
	public static final ConcurrentLinkedQueue<TextureDownloadThread> textureQueue = new ConcurrentLinkedQueue<TextureDownloadThread>();
	public static void downloadTexture(String Url) {
		if (isTextureDownloading(Url)) {
			return;
		}
		if (isTextureDownloaded(Url)) {
			return;
		}
		TextureDownloadThread thread = new TextureDownloadThread(Url, BukkitContrib.getFileName(Url));
		thread.start();
		textureQueue.add(thread);
	}
	
	public static boolean isTextureDownloading(String Url) {
		Iterator<TextureDownloadThread> i = textureQueue.iterator();
		while(i.hasNext()) {
			TextureDownloadThread next = i.next();
			if (next.getUrl().equals(Url)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTextureDownloaded(String Url) {
		if (isTextureDownloading(Url)) {
			return false;
		}
		File directory = new File(BukkitContribCache.getTextureCacheDirectory(), "temp");
		if (!directory.exists()) {
			directory.mkdir();
		}
		File download = new File(directory, BukkitContrib.getFileName(Url));
		return download.exists();
	}
	
	public static String getTextureFromUrl(String Url) {
		if (!isTextureDownloaded(Url)) {
			return null;
		}
		File directory = new File(BukkitContribCache.getTextureCacheDirectory(), "temp");
		if (!directory.exists()) {
			directory.mkdir();
		}
		File download = new File(directory, BukkitContrib.getFileName(Url));
		try {
			return download.getCanonicalPath();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void onTick() {
		Iterator<TextureDownloadThread> i = textureQueue.iterator();
		while(i.hasNext()) {
			TextureDownloadThread next = i.next();
			i.remove();
		}
	}

}