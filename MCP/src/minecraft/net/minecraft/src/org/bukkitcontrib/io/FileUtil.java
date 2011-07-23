package org.bukkitcontrib.io;

import java.io.File;
import org.apache.commons.io.FileUtils;
import net.minecraft.src.BukkitContrib;

public class FileUtil {
	public static File getCacheDirectory() {
		File directory = new File(BukkitContrib.getGameInstance().getMinecraftDir(), "bukkitcontrib");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}

	public static File getTempDirectory() {
		File directory = new File(getCacheDirectory(), "temp");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}

	public static void deleteTempDirectory() {
		try {
			FileUtils.deleteDirectory(getTempDirectory());
		}
		catch (Exception e) {}
		try {
			FileUtils.deleteDirectory(getTextureCacheDirectory());
		}
		catch (Exception e) {}
	}

	public static File getAudioCacheDirectory() {
		File directory = new File(getCacheDirectory(), "audiocache");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}

	public static File getTextureCacheDirectory() {
		File directory = new File(getCacheDirectory(), "texturecache");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}

	public static File getTexturePackDirectory() {
		File directory = new File(BukkitContrib.getGameInstance().getMinecraftDir(), "texturepacks");
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}
	
	public static String getFileName(String Url) {
		int slashIndex = Url.lastIndexOf('/');
		int dotIndex = Url.lastIndexOf('.', slashIndex);
		if (dotIndex == -1 || dotIndex < slashIndex) {
				return Url.substring(slashIndex + 1).replaceAll("%20", " ");
		}
		return Url.substring(slashIndex + 1, dotIndex).replaceAll("%20", " ");
	}
}