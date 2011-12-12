package org.getspout.commons.addon;

import org.getspout.commons.addon.Addon;

public interface AddonStore {
	public void downloadAddon(int databaseId, DownloadEventDelegate delegate);
	public void downloadAddon(String name, DownloadEventDelegate delegate);
	public boolean hasUpdate(Addon addon);
	public boolean hasInternetAccess(Addon addon);
	public long getQuota(Addon addon);
	public boolean isEnabled(Addon addon);
	
	public abstract class DownloadEventDelegate {
		public abstract void onDownloadFinished(Addon addon);
		public abstract void onDownloadFailure(Exception e, int databaseId, String name);
	}
}
