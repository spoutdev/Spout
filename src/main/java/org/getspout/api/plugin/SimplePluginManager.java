/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getspout.api.Game;
import org.getspout.api.addon.java.JavaAddonLoader;
import org.getspout.api.command.AddonCommandYamlParser;
import org.getspout.api.command.Command;
import org.getspout.api.event.Event;

public class SimplePluginManager implements PluginManager {

	private Game client;
	private final Map<Pattern, PluginLoader> fileAssociations = new HashMap<Pattern, PluginLoader>();
	private final List<Plugin> addons = new ArrayList<Plugin>();
	private final Map<String, Plugin> lookupNames = new HashMap<String, Plugin>();
	private static File updateDirectory = null;
	private final SimpleSecurityManager securityManager;
	private final double key;

	public SimplePluginManager(Game instance, SimpleSecurityManager manager, double key) {
		client = instance;
		
		this.key = key;
		securityManager = manager;
	}

	/**
	 * Registers the specified addon loader
	 * 
	 * @param loader Class name of the AddonLoader to register
	 * @throws IllegalArgumentException Thrown when the given Class is not a valid AddonLoader
	 */
	public void registerInterface(Class<? extends PluginLoader> loader) throws IllegalArgumentException {
		if (!loader.equals(JavaAddonLoader.class)){
			throw new UnsupportedOperationException("Spoutcraft does not currently support non-standard addon loaders. :(");
		}
		PluginLoader instance;

		if (PluginLoader.class.isAssignableFrom(loader)) {
			Constructor<? extends PluginLoader> constructor;

			try {
				constructor = loader.getConstructor(Game.class, SimpleSecurityManager.class, double.class);
				instance = constructor.newInstance(client, securityManager, key);
			} catch (NoSuchMethodException ex) {
				String className = loader.getName();

				throw new IllegalArgumentException(String.format("Class %s does not have a public %s(Spoutcraft) constructor", className, className), ex);
			} catch (Exception ex) {
				throw new IllegalArgumentException(String.format("Unexpected exception %s while attempting to construct a new instance of %s", ex.getClass().getName(), loader.getName()), ex);
			}
		} else {
			throw new IllegalArgumentException(String.format("Class %s does not implement interface AddonLoader", loader.getName()));
		}

		Pattern[] patterns = instance.getAddonFileFilters();

		synchronized (this) {
			for (Pattern pattern : patterns) {
				fileAssociations.put(pattern, instance);
			}
		}
	}
	
	/**
	 * Loads the addons contained within the specified directory
	 * 
	 * @param directory Directory to check for addons
	 * @return A list of all addons loaded
	 */
	public Plugin[] loadAddons(File directory) {
		List<Plugin> result = new ArrayList<Plugin>();
		File[] files = directory.listFiles();

		boolean allFailed = false;
		boolean finalPass = false;

		LinkedList<File> filesList = new LinkedList<File>(Arrays.asList(files));

		if (!(client.getUpdateFolder() == null)) {
			updateDirectory = client.getUpdateFolder();
		}

		while (!allFailed || finalPass) {
			allFailed = true;
			Iterator<File> itr = filesList.iterator();

			while (itr.hasNext()) {
				File file = itr.next();
				Plugin addon = null;

				try {
					addon = loadAddon(file, finalPass);
					itr.remove();
				} catch (UnknownDependencyException ex) {
					if (finalPass) {
						safelyLog(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': " + ex.getMessage(), ex);
						safelyLog(Level.SEVERE, "Unknown dependancy: " + ex.getMessage(), ex);
						itr.remove();
					} else {
						addon = null;
					}
				} catch (InvalidPluginException ex) {
					safelyLog(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': " + ex.getMessage(), ex.getCause());
					itr.remove();
				} catch (InvalidDescriptionException ex) {
					safelyLog(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': " + ex.getMessage(), ex);
					itr.remove();
				}

				if (addon != null) {
					result.add(addon);
					allFailed = false;
					finalPass = false;
				}
			}
			if (finalPass) {
				break;
			} else if (allFailed) {
				finalPass = true;
			}
		}

		return result.toArray(new Plugin[result.size()]);
	}

	/**
	 * Loads the addon in the specified file
	 * 
	 * File must be valid according to the current enabled Addon interfaces
	 * 
	 * @param file File containing the addon to load
	 * @return The Addon loaded, or null if it was invalid
	 * @throws InvalidPluginException Thrown when the specified file is not a valid addon
	 * @throws InvalidDescriptionException Thrown when the specified file contains an invalid description
	 */
	public synchronized Plugin loadAddon(File file) throws InvalidPluginException, InvalidDescriptionException, UnknownDependencyException {
		return loadAddon(file, true);
	}

	/**
	 * Loads the addon in the specified file
	 * 
	 * File must be valid according to the current enabled Addon interfaces
	 * 
	 * @param file File containing the addon to load
	 * @param ignoreSoftDependencies Loader will ignore soft dependencies if this flag is set to true
	 * @return The Addon loaded, or null if it was invalid
	 * @throws InvalidPluginException Thrown when the specified file is not a valid addon
	 * @throws InvalidDescriptionException Thrown when the specified file contains an invalid description
	 */
	public synchronized Plugin loadAddon(File file, boolean ignoreSoftDependencies) throws InvalidPluginException, InvalidDescriptionException, UnknownDependencyException {
		securityManager.lock(key);
		File updateFile = null;

		if (updateDirectory != null && updateDirectory.isDirectory() && (updateFile = new File(updateDirectory, file.getName())).isFile()) {
			//TODO fix
			//if (FileUtil.copy(updateFile, file)) {
			//	updateFile.delete();
			//}
		}

		Set<Pattern> filters = fileAssociations.keySet();
		Plugin result = null;

		for (Pattern filter : filters) {
			String name = file.getName();
			Matcher match = filter.matcher(name);

			if (match.find()) {
				PluginLoader loader = fileAssociations.get(filter);

				result = loader.loadAddon(file, ignoreSoftDependencies);
			}
		}

		if (result != null) {
			addons.add(result);
			lookupNames.put(result.getDescription().getName(), result);
		}
		securityManager.unlock(key);
		return result;
	}

	/**
	 * Checks if the given addon is loaded and returns it when applicable
	 * 
	 * Please note that the name of the addon is case-sensitive
	 * 
	 * @param name Name of the addon to check
	 * @return Addon if it exists, otherwise null
	 */
	public synchronized Plugin getAddon(String name) {
		return lookupNames.get(name);
	}
	
	public synchronized Plugin getOrCreateAddon(String name) {
		if(!lookupNames.containsKey(name)) {
			addFakeAddon(new ServerPlugin(name, null, null));
		}
		return lookupNames.get(name);
	}

	public synchronized Plugin[] getAddons() {
		return addons.toArray(new Plugin[0]);
	}

	/**
	 * Checks if the given addon is enabled or not
	 * 
	 * Please note that the name of the addon is case-sensitive.
	 * 
	 * @param name Name of the addon to check
	 * @return true if the addon is enabled, otherwise false
	 */
	public boolean isAddonEnabled(String name) {
		Plugin addon = getAddon(name);

		return isAddonEnabled(addon);
	}

	/**
	 * Checks if the given addon is enabled or not
	 * 
	 * @param addon Addon to check
	 * @return true if the addon is enabled, otherwise false
	 */
	public boolean isAddonEnabled(Plugin addon) {
		if ((addon != null) && (addons.contains(addon))) {
			return addon.isEnabled();
		} else {
			return false;
		}
	}

	public void enableAddon(final Plugin addon) {
		if (addon instanceof ServerPlugin) {
			return;
		}
		if (!addon.isEnabled()) {
			//Check if disabled by user
			//TODO fix
			//if(!Spoutcraft.getAddonStore().isEnabled(addon)) {
			//	System.out.println("Addon "+addon.getDescription().getName()+" could not be loaded because it is disabled.");
			//	return;
			//}
			securityManager.lock(key);
			List<Command> addonCommands = AddonCommandYamlParser.parse(addon);

			try {
				addon.getAddonLoader().enableAddon(addon);
			} catch (Throwable ex) {
				safelyLog(Level.SEVERE, "Error occurred (in the addon loader) while enabling " + addon.getDescription().getFullName() + " (Is it up to date?): " + ex.getMessage(), ex);
			}
			securityManager.unlock(key);
		}
	}

	public void disableAddons() {
		for (Plugin addon : getAddons()) {
			disableAddon(addon);
		}
	}

	public void disableAddon(final Plugin addon) {
		if (addon instanceof ServerPlugin) {
			return;
		}
		if (addon.isEnabled()) {
			securityManager.lock(key);
			try {
				addon.getAddonLoader().disableAddon(addon);
			} catch (Throwable ex) {
				safelyLog(Level.SEVERE, "Error occurred (in the addon loader) while disabling " + addon.getDescription().getFullName() + " (Is it up to date?): " + ex.getMessage(), ex);
			}
			securityManager.unlock(key);
		}
	}

	public void clearAddons() {
		synchronized (this) {
			disableAddons();
			addons.clear();
			lookupNames.clear();
			//HandlerList.clearAll();
		}
	}

	/**
	 * Call an event.
	 * 
	 * @param <TEvent> Event subclass
	 * @param event Event to handle
	 * @author lahwran
	 */
	public <TEvent extends Event> TEvent callEvent(TEvent event) {
/*		TODO: fix
		HandlerList<TEvent> handlerlist = event.getHandlers();
		handlerlist.bake();

		Listener<TEvent>[][] handlers = handlerlist.handlers;
		int[] handlerids = handlerlist.handlerids;
		
		//We wouldn't want to open the lock prematurely if it was locked by the caller, would we now? :)
		boolean wasLocked = securityManager.isLocked();
		if (!wasLocked) {
			securityManager.lock(key);
		}

		for (int arrayidx = 0; arrayidx < handlers.length; arrayidx++) {

			// if the order slot is even and the event has stopped propogating
			if (event.isCancelled() && (handlerids[arrayidx] & 1) == 0)
				continue; // then don't call this order slot

			for (int handler = 0; handler < handlers[arrayidx].length; handler++) {
				try {
					
					handlers[arrayidx][handler].onEvent(event);

				} catch (Throwable t) {
					System.err.println("Error while passing event " + event);
					t.printStackTrace();
				}
			}
		}
		if (!wasLocked) {
			securityManager.unlock(key);
		}*/
		return event;
	}
	
	private void safelyLog(Level level, String message, Throwable ex) {
		boolean relock = false;
		if (securityManager.isLocked()){
			relock = true;
			securityManager.unlock(key);
		}
		client.getLogger().log(level, message, ex);
		if (relock) {
			securityManager.lock(key);
		}
	}
	
	public void addFakeAddon(ServerPlugin addon) {
		addons.add(addon);
		addon.setEnabled(true);
		lookupNames.put(addon.getDescription().getName(), addon);
	}
	
	public ThreadGroup getSecurityThreadGroup() {
		return securityManager.getSecurityThreadGroup();
	}

}
