package org.getspout.spout.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.Plugin;
import org.getspout.spout.io.FileUtil;
import org.getspout.spoutapi.player.FileManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleFileManager implements FileManager {
	private Map<Plugin,  List<File>> preLoginCache = new HashMap<Plugin,  List<File>>();
	private Map<Plugin,  List<String>> preLoginUrlCache = new HashMap<Plugin,  List<String>>();
	private Map<Plugin, List<String>> cachedFiles = new HashMap<Plugin,  List<String>>();
	private static final String[] validExtensions = {"txt", "yml", "xml", "png", "jpg", "ogg", "midi", "wav", "zip"};
	
	
	public void onPlayerJoin(SpoutPlayer player) {
		//TODO send files
	}

	@Override
	public List<String> getCache(Plugin plugin) {
		List<String> cache = cachedFiles.get(plugin);
		if (cache == null) {
			return new ArrayList<String>(1);
		}
		return cache;
	}

	@Override
	public boolean addToPreLoginCache(Plugin plugin, File file) {
		if (canCache(file)) {
			List<File> cache = preLoginCache.get(plugin);
			if (cache == null) {
				cache = new ArrayList<File>();
			}
			cache.add(file);
			preLoginCache.put(plugin, cache);
			return true;
		}
		return false;
	}

	@Override
	public boolean addToPreLoginCache(Plugin plugin, String fileUrl) {
		if (canCache(fileUrl)) {
			List<String> cache = preLoginUrlCache.get(plugin);
			if (cache == null) {
				cache = new ArrayList<String>();
			}
			cache.add(fileUrl);
			preLoginUrlCache.put(plugin, cache);
			return true;
		}
		return false;
	}

	@Override
	public boolean addToPreLoginCache(Plugin plugin, Collection<File> files) {
		for (File file: files) {
			if (!canCache(file)) {
				return false;
			}
		}
		List<File> cache = preLoginCache.get(plugin);
		if (cache == null) {
			cache = new ArrayList<File>();
		}
		cache.addAll(files);
		preLoginCache.put(plugin, cache);
		return true;
	}

	@Override
	public boolean addToPreLoginCache(Plugin plugin, List<String> fileUrls) {
		for (String file: fileUrls) {
			if (!canCache(file)) {
				return false;
			}
		}
		List<String> cache = preLoginUrlCache.get(plugin);
		if (cache == null) {
			cache = new ArrayList<String>();
		}
		cache.addAll(fileUrls);
		preLoginUrlCache.put(plugin, cache);
		return true;
	}

	@Override
	public boolean addToCache(Plugin plugin, File file) {
		if (addToPreLoginCache(plugin, file)) {
			//TODO send packet
			return true;
		}
		return false;
	}

	@Override
	public boolean addToCache(Plugin plugin, String fileUrl) {
		if (addToPreLoginCache(plugin, fileUrl)) {
			//TODO send packet
			return true;
		}
		return false;
	}

	@Override
	public boolean addToCache(Plugin plugin, Collection<File> files) {
		if (addToPreLoginCache(plugin, files)) {
			//TODO send packet
			return true;
		}
		return false;
	}

	@Override
	public boolean addToCache(Plugin plugin, List<String> fileUrls) {
		if (addToPreLoginCache(plugin, fileUrls)) {
			//TODO send packet
			return true;
		}
		return false;
	}

	@Override
	public void removeFromCache(Plugin plugin, String file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFromCache(Plugin plugin, List<String> file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canCache(File file) {
		String filename = FileUtil.getFileName(file.getPath());
		return FilenameUtils.isExtension(filename, validExtensions);
	}

	@Override
	public boolean canCache(String fileUrl) {
		String filename = FileUtil.getFileName(fileUrl);
		return FilenameUtils.isExtension(filename, validExtensions);
	}

}
