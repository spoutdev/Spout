package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import org.bukkitcontrib.io.FileDownloadThread;
import org.bukkitcontrib.io.FileUtil;
import org.bukkitcontrib.io.Download;

public class CustomTextureManager {
	public static void downloadTexture(String Url) {
		if (!isTextureDownloading(url) && !isTextureDownloaded(url)) {
			Download download = new Download(FileUtil.getFileName(url), FileUtil.getTextureCacheDirectory(), url, null);
			FileDownloadThread.getInstance().addToDownloadQueue(download);
		}
	}
	
	public static boolean isTextureDownloading(String Url) {
		return FileDownloadThread.getInstance().isDownloading(url);
			}
	
	public static boolean isTextureDownloaded(String Url) {
		return (new File(FileUtil.getTextureCacheDirectory(), FileUtil.getFileName(Url))).exists();
		}
	
	public static String getTextureFromUrl(String Url) {
		if (!isTextureDownloaded(Url)) {
			return null;
		}
		File download = new File(FileUtil.getTextureCacheDirectory(), FileUtil.getFileName(Url));
		try {
			return download.getCanonicalPath();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}