/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.spout.api.Game;
import org.spout.api.UnsafeMethod;
import org.spout.api.exception.InvalidDescriptionFileException;
import org.spout.api.exception.InvalidPluginException;
import org.spout.api.exception.UnknownDependencyException;
import org.spout.api.exception.UnknownSoftDependencyException;
import org.spout.api.plugin.security.CommonSecurityManager;

public class CommonPluginLoader implements PluginLoader {

	public static final String YAML_SPOUT = "spoutplugin.yml";
	public static final String YAML_OTHER = "plugin.yml";

	protected final Game game;

	private final Pattern[] patterns;
	private final CommonSecurityManager manager;
	private final double key;
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private final Map<String, CommonClassLoader> loaders = new HashMap<String, CommonClassLoader>();

	public CommonPluginLoader(final Game game, final CommonSecurityManager manager, final double key) {
		this.game = game;
		this.manager = manager;
		this.key = key;
		patterns = new Pattern[] {Pattern.compile("\\.jar$")};
	}

	public Pattern[] getPatterns() {
		return patterns;
	}

	@UnsafeMethod
	public void enablePlugin(Plugin paramPlugin) {
		if (!CommonPlugin.class.isAssignableFrom(paramPlugin.getClass())) {
			throw new IllegalArgumentException("Cannot enable plugin with this PluginLoader as it is of the wrong type!");
		}
		if (!paramPlugin.isEnabled()) {
			CommonPlugin cp = (CommonPlugin) paramPlugin;
			String name = cp.getDescription().getName();

			if (!loaders.containsKey(name)) {
				loaders.put(name, (CommonClassLoader) cp.getClassLoader());
			}

			try {
				cp.setEnabled(true);
				cp.onEnable();
			} catch (Exception e) {
				game.getLogger().log(Level.SEVERE, new StringBuilder().append("An error occured when enabling '").append(paramPlugin.getDescription().getFullName()).append("': ").append(e.getMessage()).toString(), e);
			}

			// TODO call PluginEnableEvent
		}
	}

	@UnsafeMethod
	public void disablePlugin(Plugin paramPlugin) {
		if (!CommonPlugin.class.isAssignableFrom(paramPlugin.getClass())) {
			throw new IllegalArgumentException("Cannot disable plugin with this PluginLoader as it is of the wrong type!");
		}
		if (paramPlugin.isEnabled()) {
			CommonPlugin cp = (CommonPlugin) paramPlugin;
			String name = cp.getDescription().getName();

			if (!loaders.containsKey(name)) {
				loaders.put(name, (CommonClassLoader) cp.getClassLoader());
			}

			try {
				cp.setEnabled(false);
				cp.onDisable();
			} catch (Exception e) {
				game.getLogger().log(Level.SEVERE, new StringBuilder().append("An error occurred when disabling plugin '").append(paramPlugin.getDescription().getFullName()).append("' : ").append(e.getMessage()).toString(), e);
			}

			// TODO call PluginDisableEvent
		}

	}

	public Plugin loadPlugin(File paramFile) throws InvalidPluginException, InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException {
		return loadPlugin(paramFile, false);
	}

	public Plugin loadPlugin(File paramFile, boolean ignoresoftdepends) throws InvalidPluginException, InvalidPluginException, UnknownDependencyException, InvalidDescriptionFileException {
		CommonPlugin result = null;
		PluginDescriptionFile desc = null;

		desc = getDescription(paramFile);

		File dataFolder = new File(paramFile.getParentFile(), desc.getName());

		processDependencies(desc);

		if (!ignoresoftdepends) {
			processSoftDependencies(desc);
		}

		CommonClassLoader loader = null;
		try {
			URL[] urls = new URL[1];
			urls[0] = paramFile.toURI().toURL();

			loader = game.getPlatform() == Platform.CLIENT ? new ClientClassLoader(this, urls, getClass().getClassLoader()) : new CommonClassLoader(this, urls, getClass().getClassLoader());
			Class<?> main = Class.forName(desc.getMain(), true, loader);
			Class<? extends CommonPlugin> plugin = main.asSubclass(CommonPlugin.class);

			boolean locked = manager.lock(key);

			Constructor<? extends CommonPlugin> constructor = plugin.getConstructor();

			result = constructor.newInstance();

			result.initialize(this, game, desc, dataFolder, paramFile, loader);

			if (!locked) {
				manager.unlock(key);
			}
		} catch (Exception e) {
			throw new InvalidPluginException(e);
		}

		loaders.put(desc.getName(), loader);

		return result;
	}

	/**
	 * @param desc Plugin description element
	 * @throws UnknownSoftDependencyException
	 */
	protected void processSoftDependencies(PluginDescriptionFile desc) throws UnknownSoftDependencyException {
		List<String> softdepend = desc.getSoftDepends();
		if (softdepend == null) {
			softdepend = new ArrayList<String>();
		}

		for (String depend : softdepend) {
			if (loaders == null) {
				throw new UnknownSoftDependencyException(depend);
			}
			if (!loaders.containsKey(depend)) {
				throw new UnknownSoftDependencyException(depend);
			}
		}
	}

	/**
	 * @param desc Plugin description element
	 * @throws UnknownDependencyException
	 */
	protected void processDependencies(PluginDescriptionFile desc) throws UnknownDependencyException {
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
	}

	/**
	 * @param param File Plugin file object
	 * @return The current plugins description element.
	 * 
	 * @throws InvalidPluginException
	 * @throws InvalidDescriptionFileException
	 */
	protected PluginDescriptionFile getDescription(File paramFile) throws InvalidPluginException, InvalidDescriptionFileException {
		if (!paramFile.exists()) {
			throw new InvalidPluginException(new StringBuilder().append(paramFile.getName()).append(" does not exist!").toString());
		}

		PluginDescriptionFile desc = null;
		JarFile jar = null;
		InputStream in = null;
		try {
			// spout plugin configuration file
			jar = new JarFile(paramFile);
			JarEntry entry = jar.getJarEntry(YAML_SPOUT);

			// fallback: other plugin configuration file
			if (entry == null) {
				entry = jar.getJarEntry(YAML_OTHER);
			}

			if (entry == null) {
				throw new InvalidPluginException("Jar has no plugin.yml or spoutplugin.yml!");
			}

			in = jar.getInputStream(entry);
			desc = new PluginDescriptionFile(in);
		} catch (IOException e) {
			throw new InvalidPluginException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					game.getLogger().log(Level.WARNING, "Problem closing input stream", e);
				}
			}
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					game.getLogger().log(Level.WARNING, "Problem closing jar input stream", e);
				}
			}
		}
		return desc;
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
