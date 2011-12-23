package org.getspout.api.plugin;

/**
 * Determines the load order for Plugins
 *
 */
public enum LoadOrder {
	
	/**
	 * Loaded after the World is loaded
	 */
	POSTWORLD,
	
	/**
	 * Loaded at Server/Client startup
	 */
	STARTUP;
}
