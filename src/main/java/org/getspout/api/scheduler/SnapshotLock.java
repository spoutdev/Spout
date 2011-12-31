package org.getspout.api.scheduler;

import org.getspout.api.plugin.Plugin;

/**
 * A class to allow non-pulsed threads to synchronize with the pulsed thread system
 */
public interface SnapshotLock {

	/**
	 * Readlocks the stable snapshot.
	 * 
	 * This method will prevent server ticks from completing, so any locks should be short
	 * 
	 * @param plugin the plugin
	 */
	public void readLock(Plugin plugin);
	
	/**
	 * Attempts to readlock the stable snapshot and returns immediately
	 * 
	 * This method will prevent server ticks from completing, so any locks should be short
	 * 
	 * @param plugin the plugin
	 */
	public boolean readTryLock(Plugin plugin);
	
	/**
	 * Releases a previous readlock
	 * 
	 * @param plugin the plugin
	 */
	public void readUnlock(Plugin plugin);
	
}
