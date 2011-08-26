package org.getspout.spout.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.io.CRCStore;
import org.getspout.spoutapi.io.CRCStore.URLCheck;
import org.getspout.spoutapi.io.CRCStoreRunnable;
import org.getspout.spoutapi.io.FileUtil;
import org.getspout.spoutapi.packet.PacketCacheDeleteFile;
import org.getspout.spoutapi.packet.PacketPreCacheCompleted;
import org.getspout.spoutapi.packet.PacketPreCacheFile;
import org.getspout.spoutapi.player.FileManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleFileManager implements FileManager {
	private Map<Plugin,  List<File>> preLoginCache = new HashMap<Plugin,  List<File>>();
	private Map<Plugin,  List<String>> preLoginUrlCache = new HashMap<Plugin,  List<String>>();
	private Map<Plugin, List<String>> cachedFiles = new HashMap<Plugin,  List<String>>();
	private static final String[] validExtensions = {"txt", "yml", "xml", "png", "jpg", "ogg", "midi", "wav", "zip"};
	
	public void onPlayerJoin(final SpoutPlayer player) {
		if (player.isSpoutCraftEnabled()) {
			Iterator<Entry<Plugin, List<File>>> i = preLoginCache.entrySet().iterator();
			while (i.hasNext()) {
				Entry<Plugin, List<File>> next = i.next();
				for (File file : next.getValue()) {
					long crc = -1;
					try {
						byte[] data = new byte[FileUtils.readFileToByteArray(file).length];
						crc = FileUtil.getCRC(file, data);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (crc !=-1) {
						player.sendPacket(new PacketPreCacheFile(next.getKey().getDescription().getName(), file.getPath(), crc, false));
					}
				}
			}
			LinkedList<Thread> urlThreads = new LinkedList<Thread>();
			Iterator<Entry<Plugin, List<String>>> j = preLoginUrlCache.entrySet().iterator();
			while (j.hasNext()) {
				final Entry<Plugin, List<String>> next = j.next();
				for (final String url : next.getValue()) {
					URLCheck urlCheck = new URLCheck(url, new byte[4096], new CRCStoreRunnable() {
						
						Long CRC;
						
						public void setCRC(Long CRC) {
							this.CRC = CRC;
						}
						
						public void run() {
							player.sendPacket(new PacketPreCacheFile(next.getKey().getDescription().getName(), url, CRC, true));
						}
						
					});
					urlCheck.setName(url);
					urlCheck.start();
					urlThreads.add(urlCheck);
				}
			}
			new URLCheckJoin(urlThreads, player).start();
		}
	}
	
	private class URLCheckJoin extends Thread {
		
		private final List<Thread> threads;
		private final SpoutPlayer player;
		
		public URLCheckJoin(List<Thread> threads, SpoutPlayer player) {
			this.threads = threads;
			this.player = player;
		}
		
		public void run() {
			while(!threads.isEmpty()) {
				Iterator<Thread> i = threads.iterator();
				while (i.hasNext()) {
					Thread t = i.next();
					try {
						t.join();
						i.remove();
					} catch (InterruptedException ie) {
					}
				}
			}
			player.sendPacket(new PacketPreCacheCompleted());
		}
		
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
			String fileName = FileUtil.getFileName(file.getPath());
			long crc = -1;
			try {
				crc = CRCStore.getCRC(fileName, FileUtils.readFileToByteArray(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (crc !=-1) {
				for (SpoutPlayer player : SpoutManager.getOnlinePlayers()) {
					if (player.isSpoutCraftEnabled()) {
						player.sendPacket(new PacketPreCacheFile(plugin.getDescription().getName(), file.getPath(), crc, false));
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean addToCache(final Plugin plugin, final String fileUrl) {
		if (addToPreLoginCache(plugin, fileUrl)) {
			URLCheck urlCheck = new URLCheck(fileUrl, new byte[4096], new CRCStoreRunnable() {
				
				Long CRC;
				
				public void setCRC(Long CRC) {
					this.CRC = CRC;
				}
				
				public void run() {
					for (SpoutPlayer player : SpoutManager.getOnlinePlayers()) {
						if (player.isSpoutCraftEnabled()) {
							player.sendPacket(new PacketPreCacheFile(plugin.getDescription().getName(), fileUrl, CRC, true));
						}
					}
				}
				
			});
			urlCheck.start();
		}
		return false;
	}

	@Override
	public boolean addToCache(Plugin plugin, Collection<File> files) {
		boolean success = true;
		for (File file : files) {
			if (!addToCache(plugin, file)) {
				success = false;
			}
		}
		return success;
	}

	@Override
	public boolean addToCache(Plugin plugin, List<String> fileUrls) {
		boolean success = true;
		for (String file : fileUrls) {
			if (!addToCache(plugin, file)) {
				success = false;
			}
		}
		return success;
	}

	@Override
	public void removeFromCache(Plugin plugin, String file) {
		List<File> cache = preLoginCache.get(plugin);
		if (cache != null) {
			Iterator<File> i = cache.iterator();
			while (i.hasNext()) {
				File next = i.next();
				String fileName = FileUtil.getFileName(next.getPath());
				if (fileName.equals(file)) {
					i.remove();
				}
			}
		}
		List<String> urlCache = preLoginUrlCache.get(plugin);
		if (urlCache != null) {
			Iterator<String> i = urlCache.iterator();
			while (i.hasNext()) {
				String next = i.next();
				String fileName = FileUtil.getFileName(next);
				if (fileName.equals(file)) {
					i.remove();
				}
			}
		}
		for (SpoutPlayer player : SpoutManager.getOnlinePlayers()) {
			if (player.isSpoutCraftEnabled()) {
				player.sendPacket(new PacketCacheDeleteFile(plugin.getDescription().getName(), file));
			}
		}
	}

	@Override
	public void removeFromCache(Plugin plugin, List<String> files) {
		for (String file : files) {
			removeFromCache(plugin, file);
		}
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
