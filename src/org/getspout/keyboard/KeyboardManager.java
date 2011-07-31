package org.getspout.keyboard;

import org.bukkit.plugin.Plugin;

public interface KeyboardManager {
	/**
	 * Get's the number of key bindings associated with this key
	 * @param key to check against
	 * @return number of key bindings
	 */
	public int getNumKeyBindings(Keyboard key);
	
	/**
	 * Adds a key binding to a particular key
	 * @param key to bind to
	 * @param keyBinding to bind to the key
	 * @param plugin for this binding
	 */
	public void addKeyBinding(Keyboard key, KeyboardBinding keyBinding, Plugin plugin);
	
	/**
	 * Removes a key binding from a particular key
	 * @param key to remove the binding from
	 * @param keyBindingClass that the keyboardbinding is an instanceof 
	 * @param plugin for this binding
	 */
	public void removeKeyBinding(Keyboard key, Class<? extends KeyboardBinding> keyBindingClass, Plugin plugin);
	
	/**
	 * Removes all the keyboard bindings associated with this particular plugin. Automatically called onPluginDisable.
	 * @param plugin to remove key bindings for
	 */
	public void removeAllKeyBindings(Plugin plugin);
}
