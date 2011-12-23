package org.getspout.api.plugin;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.getspout.api.Game;/*
import org.getspout.api.event.Event;
import org.getspout.api.event.HandlerList;*/
import org.getspout.api.plugin.exceptions.InvalidDescriptionFileException;
import org.getspout.api.plugin.exceptions.InvalidPluginException;
import org.getspout.api.plugin.exceptions.UnknownDependencyException;
import org.getspout.api.plugin.security.CommonSecurityManager;

public class CommonPluginManager implements PluginManager {

	private final Game game;
	private final CommonSecurityManager manager;
	private final double key;
	private File updateDir;
	private final Map<Pattern, PluginLoader> loaders = new HashMap<Pattern, PluginLoader>();
	private final Map<String, Plugin> names = new HashMap<String, Plugin>();
	private final List<Plugin> plugins = new ArrayList<Plugin>();
	
	public CommonPluginManager(final Game game, final CommonSecurityManager manager, final double key) {
		this.game = game;
		this.manager = manager;
		this.key = key;
	}
	
	public void registerPluginLoader(Class<? extends PluginLoader> loader) {
		PluginLoader instance = null;
		
		try {
			Constructor<? extends PluginLoader> constructor = loader.getConstructor();
			instance = constructor.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("Error registering plugin loader!", e);
		}
		
		synchronized(this) {
			for (Pattern pattern : instance.getPatterns()) {
				loaders.put(pattern, instance);
			}			
		}
	}
	
	public Plugin getPlugin(String plugin) {
		return names.get(plugin);
	}

	public Plugin[] getPlugins() {
		return plugins.toArray(new Plugin[plugins.size()]);
	}

	public synchronized Plugin loadPlugin(File paramFile) throws InvalidPluginException, InvalidDescriptionFileException, UnknownDependencyException {
		return loadPlugin(paramFile, false);
	}
	
	public synchronized Plugin loadPlugin(File paramFile, boolean ignoresoftdepends) throws InvalidPluginException, InvalidDescriptionFileException, UnknownDependencyException {
		boolean locked = manager.lock(key);
		File update = null;
		
		if (updateDir != null && updateDir.isDirectory()) {
			update = new File(updateDir, paramFile.getName());
			if (update.exists() && update.isFile()) {
				try {
					FileUtils.copyFile(update, paramFile);
				} catch (IOException e) {
					safelyLog(Level.SEVERE, new StringBuilder().append("Error copying file '").append(update.getPath()).append("' to its new destination at '").append(paramFile.getPath()).append("': ").append(e.getMessage()).toString(), e);
				}
				update.delete();
			}
		}
		
		Set<Pattern> patterns = loaders.keySet();
		Plugin result = null;
		
		for (Pattern pattern : patterns) {
			String name = paramFile.getName();
			Matcher m = pattern.matcher(name);
			
			if (m.find()) {
				PluginLoader loader = loaders.get(pattern);
				result = loader.loadPlugin(paramFile, ignoresoftdepends);
				
				if (result != null) 
					break;
			}
		}
		
		if (result != null) {
			plugins.add(result);
			names.put(result.getDescription().getName(), result);
		}
		
		if (locked) 
			manager.unlock(key);
		return result;
	}

	public synchronized Plugin[] loadPlugins(File paramFile) {
		
		if (!paramFile.isDirectory())
			throw new IllegalArgumentException("File parameter was not a Directory!");
		
		if (game.getUpdateFolder() != null) {
			updateDir = game.getUpdateFolder();
		}
		
		List<Plugin> result = new ArrayList<Plugin>();
		LinkedList<File> files = new LinkedList<File>(Arrays.asList(paramFile.listFiles()));
		boolean failed = false;
		boolean lastPass = false;
		
		while(!failed || lastPass) {
			failed = true;
			Iterator<File> iterator = files.iterator();
			
			while(iterator.hasNext()) {
				File file = iterator.next();
				Plugin plugin = null;
				
				if (file.isDirectory()) {
					iterator.remove();
					continue;
				}
				
				try {
					plugin = loadPlugin(file, lastPass);
					iterator.remove();
				} catch (UnknownDependencyException e) {
					if (lastPass) {
						safelyLog(Level.SEVERE, new StringBuilder().append("Unable to load '").append(file.getName()).append("' in directory '").append(paramFile.getPath()).append("': ").append(e.getMessage()).toString(), e);
						iterator.remove();
					} else {
						plugin = null;
					}
				} catch (InvalidDescriptionFileException e) {
					safelyLog(Level.SEVERE, new StringBuilder().append("Unable to load '").append(file.getName()).append("' in directory '").append(paramFile.getPath()).append("': ").append(e.getMessage()).toString(), e);
					iterator.remove();
				} catch (InvalidPluginException e) {
					safelyLog(Level.SEVERE, new StringBuilder().append("Unable to load '").append(file.getName()).append("' in directory '").append(paramFile.getPath()).append("': ").append(e.getMessage()).toString(), e);
					iterator.remove();					
				}
				
				if (plugin != null) {
					result.add(plugin);
					failed = false;
					lastPass = false;
				}
			}
			if (lastPass) {
				break;
			} else if (failed) {
				lastPass = true;
			}
		}
		
		return result.toArray(new Plugin[result.size()]);
	}

	public void disablePlugins() {
		for (Plugin plugin : plugins) {
			disablePlugin(plugin);
		}
	}

	public void clearPlugins() {
		synchronized(this) {
			disablePlugins();
			plugins.clear();
			names.clear();
			//HandlerList.unregisterAll();
		}
	}

	public void enablePlugin(Plugin plugin) {
		if (!plugin.isEnabled()) {
			boolean locked = manager.lock(key);
			
			try {
				plugin.getPluginLoader().enablePlugin(plugin);
			} catch (Exception e) {
				safelyLog(Level.SEVERE, new StringBuilder().append("An error ocurred in the Plugin Loader while enabling plugin '").append(plugin.getDescription().getFullName()).append("': ").append(e.getMessage()).toString(), e);
			}
			
			if (!locked) 
				manager.unlock(key);
		}
	}

	public void disablePlugin(Plugin plugin) {
		if (!plugin.isEnabled()) {
			boolean locked = manager.lock(key);
			
			try {
				plugin.getPluginLoader().enablePlugin(plugin);
			} catch (Exception e) {
				safelyLog(Level.SEVERE, new StringBuilder().append("An error ocurred in the Plugin Loader while enabling plugin '").append(plugin.getDescription().getFullName()).append("': ").append(e.getMessage()).toString(), e);
			}
			
			if (!locked) 
				manager.unlock(key);
		}
	}

	/*
	public <TEvent extends Event> TEvent callEvent(TEvent event) {
		// TODO Auto-generated method stub
		return null;
	}*/
	
	private void safelyLog(Level level, String message, Throwable ex) {
		boolean relock = false;
		if (manager.isLocked()){
			relock = true;
			manager.unlock(key);
		}
		game.getLogger().log(level, message, ex);
		if (relock) {
			manager.lock(key);
		}
	}

}
