package org.getspout.spout.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.util.config.Configuration;

public class CRCStore {

	private static Configuration urlCRCStore;
	private final static Object urlCRCStoreSync = new Object();

	public static void setConfigFile(Configuration config) {
		synchronized(urlCRCStoreSync) {
			urlCRCStore = config;
			urlCRCStore.load();
		}
	}
	
	private static String encodeURL(String urlString) {
		return urlString.replace(".", "*");
	}

	public static Long getCRC(String urlString, byte[] buffer) {
		if (urlString == null) {
			return null;
		}
		
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException mue) {
			return null;
		}
		
		String key = encodeURL(url.toString());
		String info;
		long modified = 0;
		long crc = 0;

		synchronized(urlCRCStoreSync) {
			info = urlCRCStore.getString(key);
			if (info != null) {

				String[] split = info.split(":");
				if (split.length == 2) {
					try {
						modified = Long.parseLong(split[0]);
						crc = Long.parseLong(split[1]);
					} catch (NumberFormatException nfe) {
					}
				}
			}
		}

		URLConnection urlConn = null;
		InputStream in = null;
		try {
			urlConn = url.openConnection();
		} catch (IOException ioe) {
			return null;
		}
		
		try {
			in = urlConn.getInputStream();

			long urlLastModified = urlConn.getLastModified();
			if (urlLastModified == modified) {
				System.out.println("Cached");
				return crc;
			} else {
				crc = FileUtil.getCRC(in, buffer);
				info = urlLastModified + ":" + crc;
				synchronized(urlCRCStoreSync) {
					urlCRCStore.setProperty(key, info);
					urlCRCStore.save();
				}
				return crc;
			}
		} catch (IOException ioe) {
			crc = FileUtil.getCRC(in, buffer);
			synchronized(urlCRCStoreSync) {
				urlCRCStore.removeProperty(key);
				urlCRCStore.save();
			}
			return crc;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

	}
	
	private static ConcurrentHashMap<String,Thread> CRCDownloads = new ConcurrentHashMap<String,Thread>();
	
	public static class URLCheck extends Thread {
		
		final String url;
		final CRCStoreRunnable runnable;
		final byte[] buffer;
		
		public URLCheck(String url, byte[] buffer, CRCStoreRunnable runnable) {
			this.url = url;
			this.runnable = runnable;
			this.buffer = buffer;
		}

		public void run() {
			
			Thread downloadThread = CRCDownloads.get(url);
			
			if (downloadThread == null) {
				Thread old = CRCDownloads.putIfAbsent(url, this);
				if (old != null) {
					downloadThread = old;
				} else {
					downloadThread = this;
				}
			}
			
			if (downloadThread != this) {
				try {
					downloadThread.join();
				} catch (InterruptedException e) {
				}
			}
			
			Long crc = null;
			crc = CRCStore.getCRC(url, buffer);

			if (crc == null) {
				crc = 0L;
			}
			
			CRCDownloads.remove(url, this);
			
			if (runnable != null) {
				runnable.setCRC(crc);
				runnable.run();
			}
		}
		
	}

	
}
