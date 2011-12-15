package org.getspout.api.plugin;

import org.getspout.api.plugin.Plugin;

public interface PluginStore {
	public void downloadAddon(int databaseId, DownloadEventDelegate delegate);
	public void downloadAddon(String name, DownloadEventDelegate delegate);
	public boolean hasUpdate(Plugin addon);
	public boolean hasInternetAccess(Plugin addon);
	public long getQuota(Plugin addon);
	public boolean isEnabled(Plugin addon);
	
	public abstract class DownloadEventDelegate {
		public abstract void onDownloadFinished(Plugin addon);
		public abstract void onDownloadFailure(Exception e, int databaseId, String name);
	}
}
