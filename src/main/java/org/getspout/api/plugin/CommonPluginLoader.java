package org.getspout.api.plugin;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.getspout.api.Game;
import org.getspout.api.UnsafeMethod;
import org.getspout.api.plugin.exceptions.InvalidDescriptionFileException;
import org.getspout.api.plugin.exceptions.InvalidPluginException;
import org.getspout.api.plugin.exceptions.UnknownDependencyException;
import org.getspout.api.plugin.exceptions.UnknownSoftDependencyException;
import org.getspout.api.plugin.security.CommonSecurityManager;

public class CommonPluginLoader implements PluginLoader {

	private final Game game;
	private final Pattern[] patterns;
	private final CommonSecurityManager manager;
	private final double key;
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private final Map<String, CommonClassLoader> loaders = new HashMap<String, CommonClassLoader>();

	public CommonPluginLoader(final Game game,
			final CommonSecurityManager manager, final double key) {
		this.game = game;
		this.manager = manager;
		this.key = key;
		patterns = new Pattern[] { Pattern.compile("\\.jar$") };
	}

	public Pattern[] getPatterns() {
		return patterns;
	}

	@UnsafeMethod
	public void enablePlugin(Plugin paramPlugin) {
		if (!CommonPlugin.class.isAssignableFrom(paramPlugin.getClass()))
			throw new IllegalArgumentException("Cannot enable plugin with this PluginLoader as it is of the wrong type!");
		if (!paramPlugin.isEnabled()) {
			CommonPlugin cp = (CommonPlugin) paramPlugin;
			String name = cp.getDescription().getName();
			
			if (!loaders.containsKey(name)) {
				loaders.put(name, (CommonClassLoader)cp.getClassLoader());
			}
			
			try {
				cp.setEnabled(true);
			} catch (Exception e) {
				game.getLogger().log(Level.SEVERE, new StringBuilder().append("An error occured when enabling '").append(paramPlugin.getDescription().getFullName()).append("': ").append(e.getMessage()).toString(), e);
			}
			
			//TODO call PluginEnableEvent
		}
	}

	@UnsafeMethod
	public void disablePlugin(Plugin paramPlugin) {
		if (!CommonPlugin.class.isAssignableFrom(paramPlugin.getClass()))
			throw new IllegalArgumentException("Cannot disable plugin with this PluginLoader as it is of the wrong type!");
		if (paramPlugin.isEnabled()) {
			CommonPlugin cp = (CommonPlugin) paramPlugin;
			String name = cp.getDescription().getName();
			
			if (!loaders.containsKey(name)) {
				loaders.put(name, (CommonClassLoader)cp.getClassLoader());
			}
			
			try {
				cp.setEnabled(false);
			} catch (Exception e) {
				game.getLogger().log(Level.SEVERE, new StringBuilder().append("An error occured when disabling plugin '").append(paramPlugin.getDescription().getFullName()).append("' : ").append(e.getMessage()).toString(), e);
			}
			
			//TODO call PluginDisableEvent
		}

	}

	public Plugin loadPlugin(File paramFile) throws InvalidPluginException,	InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException {
		return loadPlugin(paramFile, false);
	}

	public Plugin loadPlugin(File paramFile, boolean ignoresoftdepends) throws InvalidPluginException, InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException {
		CommonPlugin result = null;
		PluginDescriptionFile desc = null;
		
		if (!paramFile.exists())
			throw new InvalidPluginException(new StringBuilder().append(paramFile.getName()).append(" does not exist!").toString());
		
		JarFile jar = null;
		InputStream in = null;
		try {
			jar = new JarFile(paramFile);
			JarEntry entry = jar.getJarEntry("plugin.yml");
			
			if (entry == null) 
				throw new InvalidPluginException("Jar is missing a plugin.yml!");
			
			in = jar.getInputStream(entry);
			desc = new PluginDescriptionFile(in);
		} catch (IOException e) {
			throw new InvalidPluginException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		File dataFolder = new File(paramFile.getParentFile(), desc.getName());
		
		
		List<String> depends = desc.getDepends();
		if (depends == null) {
			depends = new ArrayList<String>();
		}
		
		for (String depend : depends) {
			if (loaders == null) {
				throw new UnknownDependencyException(depend);
			}
			if (!loaders.containsKey(depend)) {
				throw new UnknownDependencyException(depend);
			}
		}
		
		if (!ignoresoftdepends) {
			List<String> softdepend = desc.getSoftDepends();
			if (softdepend == null) {
				softdepend = new ArrayList<String>();
			}
			
			for (String depend : depends) {
				if (loaders == null) {
					throw new UnknownSoftDependencyException(depend);
				}
				if (!loaders.containsKey(depend)) {
					throw new UnknownSoftDependencyException(depend);
				}
			}
		}
		
		CommonClassLoader loader = null;
		try {
			URL[] urls = new URL[1];
			urls[0] = paramFile.toURI().toURL();
			
			loader = game.getPlatform() == Platform.CLIENT ? new ClientClassLoader(this, urls, getClass().getClassLoader()) :  new CommonClassLoader(this, urls, getClass().getClassLoader());
			Class<?> main = Class.forName(desc.getMain(), true, loader);
			Class<? extends CommonPlugin> plugin = main.asSubclass(CommonPlugin.class);
			
			boolean locked = manager.lock(key);
			
			Constructor<? extends CommonPlugin> constructor = plugin.getConstructor();
			
			result = constructor.newInstance();
			
			result.initialize(this, game, desc, dataFolder, paramFile, loader);
			
			if (!locked) 
				manager.unlock(key);
		} catch (Exception e) {
			throw new InvalidPluginException(e);
		}
		
		loaders.put(desc.getName(), loader);
		
		return result;
	}
	
	public Class<?> getClassByName(final String name) {
		Class<?> cached = classes.get(name);

		if (cached != null) {
			return cached;
		} else {
			for (String current : loaders.keySet()) {
				CommonClassLoader loader = loaders.get(current);

				try {
					cached = loader.findClass(name, false);
				} catch (ClassNotFoundException cnfe) {
				}
				if (cached != null) {
					return cached;
				}
			}
		}
		return null;
	}

	public void setClass(final String name, final Class<?> clazz) {
		if (!classes.containsKey(name)) {
			classes.put(name, clazz);
		}
	}

}
