package net.minecraft.src;
//BukkitContrib

import java.io.File;
import org.apache.commons.io.FileUtils;

public class BukkitContribCache {
	public static File getCacheDirectory() {
		File directory = new File(BukkitContrib.getGameInstance().getMinecraftDir(), "bukkitcontrib");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}
	
	public static File getAudioCacheDirectory() {
		File directory = new File(getCacheDirectory(), "audiocache");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}
	
	public static void deleteTempAudioCache() {
		File temp = new File(getAudioCacheDirectory(), "temp");
		try {
			FileUtils.deleteDirectory(temp);
		}
		catch (Exception e) {}
	}
	
	public static File getTextureCacheDirectory() {
		File directory = new File(getCacheDirectory(), "texturecache");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}
	
	public static void deleteTempTextureCache() {
		File temp = new File(getTextureCacheDirectory(), "temp");
		try {
			FileUtils.deleteDirectory(temp);
		}
		catch (Exception e) {}
	}
	
	public static File getTexturePackDirectory() {
		File directory = new File(BukkitContrib.getGameInstance().getMinecraftDir(), "texturepacks");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}
}